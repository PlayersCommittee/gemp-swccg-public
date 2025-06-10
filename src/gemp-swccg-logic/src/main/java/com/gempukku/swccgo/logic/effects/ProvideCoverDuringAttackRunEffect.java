package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackRunState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.PlayOrder;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.List;

/**
 * An effect that allows actions performed during Provide Cover step of an Attack Run.
 */
public class ProvideCoverDuringAttackRunEffect extends AbstractSubActionEffect {
    private PhysicalCard _attackRun;

    /**
     * Creates an effect that allows actions performed during Provide Cover step of an Attack Run.
     * @param action the action performing this effect
     */
    public ProvideCoverDuringAttackRunEffect(Action action) {
        super(action);
        _attackRun = action.getActionSource();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final String lightSidePlayerId = game.getLightPlayer();

        final SubAction subAction = new SubAction(_action);

        // First, Light side player identifies lead starfighter.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        Filter leadStarfighterFilter = Filters.and(Filters.owner(lightSidePlayerId), Filters.piloted, Filters.starfighter,
                                Filters.hasAttached(Filters.Proton_Torpedoes), Filters.at(Filters.Death_Star_Trench));
                        subAction.appendEffect(
                                new ChooseCardOnTableEffect(subAction, lightSidePlayerId, "Identify lead starfighter", leadStarfighterFilter) {
                                    @Override
                                    protected void cardSelected(PhysicalCard leadStarfighter) {
                                        Filter wingmenStarfighterFilter = Filters.and(Filters.owner(lightSidePlayerId), Filters.piloted, Filters.starfighter,
                                                Filters.not(leadStarfighter), Filters.at(Filters.Death_Star_Trench));
                                        Collection<PhysicalCard> wingmen = Filters.filterActive(game, _attackRun, wingmenStarfighterFilter);

                                        final AttackRunState attackRunState = (AttackRunState) gameState.getEpicEventState();
                                        attackRunState.setLeadStarfighter(leadStarfighter);
                                        attackRunState.getWingmen().addAll(wingmen);
                                        gameState.sendMessage(GameUtils.getCardLink(leadStarfighter) + " is lead starfighter" + (!wingmen.isEmpty() ? (" with wingmen " + GameUtils.getAppendedNames(wingmen)) : ""));
                                        gameState.cardAffectsCard(lightSidePlayerId, _attackRun, leadStarfighter);

                                        // Then, players take turns performing top-level actions.
                                        subAction.appendEffect(
                                                new PassthruEffect(subAction) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        if (attackRunState.getLeadStarfighter() != null) {
                                                            PlayOrder playOrder = game.getGameState().getPlayerOrder().getPlayOrder(game.getGameState().getLightPlayer(), true);
                                                            subAction.appendEffect(
                                                                    new PlayerPlaysNextActionEffect(subAction, attackRunState, playOrder, 0));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect that asks a player to choose an action or pass.
     */
    private class PlayerPlaysNextActionEffect extends AbstractSuccessfulEffect {
        private SubAction _subAction;
        private AttackRunState _attackRunState;
        private PlayOrder _playOrder;
        private int _consecutivePasses;

        /**
         * Creates a private effect that asks a player to choose an action or pass.
         * @param subAction the action
         * @param playOrder the play order
         * @param consecutivePasses the number of consecutive passes
         */
        private PlayerPlaysNextActionEffect(SubAction subAction, AttackRunState attackRunState, PlayOrder playOrder, int consecutivePasses) {
            super(subAction);
            _subAction = subAction;
            _attackRunState = attackRunState;
            _playOrder = playOrder;
            _consecutivePasses = consecutivePasses;
        }

        @Override
        protected void doPlayEffect(final SwccgGame game) {
            String playerId = _playOrder.getNextPlayer();

            // Get the top-level actions that the player can perform
            final List<Action> playableActions = game.getActionsEnvironment().getTopLevelActions(playerId);

            game.getUserFeedback().sendAwaitingDecision(playerId,
                    new CardActionSelectionDecision(1, "Choose provide cover action to play or Pass", playableActions, playerId.equals(game.getGameState().getCurrentPlayerId()), false, false, false, false) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            final Action action = getSelectedAction(result);
                            if (action != null) {
                                action.appendAfterEffect(new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (_attackRunState.getLeadStarfighter() != null) {
                                            if (action.isChoosingTargetsComplete() || action.wasCarriedOut()) {
                                                _subAction.appendEffect(
                                                        new PlayerPlaysNextActionEffect(_subAction, _attackRunState, _playOrder, 0));
                                            }
                                            // Action was aborted, check with same player again
                                            else {
                                                _playOrder.getNextPlayer();
                                                _subAction.appendEffect(
                                                        new PlayerPlaysNextActionEffect(_subAction, _attackRunState, _playOrder, _consecutivePasses));
                                            }
                                        }
                                    }
                                });
                                // Add the action to the stack
                                game.getActionsEnvironment().addActionToStack(action);
                            }
                            else {
                                playerPassed();
                            }
                        }
                    });
        }

        /**
         * This method is called when the player passes.
         */
        private void playerPassed() {
            if (_consecutivePasses + 1 < _playOrder.getPlayerCount())
                _subAction.appendEffect(
                        new PlayerPlaysNextActionEffect(_subAction, _attackRunState, _playOrder, _consecutivePasses + 1));
        }
    }
}
