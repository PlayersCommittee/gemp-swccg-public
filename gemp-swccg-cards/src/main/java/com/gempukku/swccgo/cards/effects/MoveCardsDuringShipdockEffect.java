package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ShipdockedResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect to move cards while starships are ship-docked.
 */
public class MoveCardsDuringShipdockEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _starship1;
    private PhysicalCard _starship2;
    private Collection<PhysicalCard> _cardsAboardStarships;

    /**
     * Creates an effect to move cards while starships are ship-docked.
     * @param action the action performing this effect
     * @param starship1 a starship to ship-dock
     * @param starship2 a starship to ship-dock
     */
    public MoveCardsDuringShipdockEffect(Action action, PhysicalCard starship1, PhysicalCard starship2) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _starship1 = starship1;
        _starship2 = starship2;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {

        SubAction subAction = new SubAction(_action);

        // Figure out each action that can be done while the two ships are "docked".
        _cardsAboardStarships = Filters.filterActive(game, null,
                Filters.or(Filters.aboardExceptRelatedSites(_starship1),
                        Filters.atLocation(Filters.and(Filters.siteOfStarshipOrVehicle(_starship1),
                                Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(_playerId))),
                        Filters.aboardExceptRelatedSites(_starship2),
                        Filters.atLocation(Filters.and(Filters.siteOfStarshipOrVehicle(_starship2),
                                Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(_playerId)))));

        if (!_cardsAboardStarships.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextAction(subAction));
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Emit effect result
                            game.getActionsEnvironment().emitEffectResult(new ShipdockedResult(_starship1, _playerId));
                            game.getActionsEnvironment().emitEffectResult(new ShipdockedResult(_starship2, _playerId));
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next movement action to perform during ship-docking.
     */
    private class ChooseNextAction extends AbstractSuccessfulEffect {
        private SubAction _subAction;

        /**
         * Creates an effect for choosing the next movement action to perform during ship-docking.
         * @param subAction the action
         */
        public ChooseNextAction(SubAction subAction) {
            super(subAction);
            _subAction = subAction;
        }

        @Override
        protected void doPlayEffect(final SwccgGame game) {
            List<Action> actions = new LinkedList<Action>();

            // Figure out each action that can be done while the two ships are "docked".
            _cardsAboardStarships = Filters.filterActive(game, null,
                    Filters.or(Filters.aboardExceptRelatedSites(_starship1),
                            Filters.atLocation(Filters.and(Filters.siteOfStarshipOrVehicle(_starship1),
                                    Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(_playerId))),
                            Filters.aboardExceptRelatedSites(_starship2),
                            Filters.atLocation(Filters.and(Filters.siteOfStarshipOrVehicle(_starship2),
                                    Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(_playerId)))));

            for (PhysicalCard cardAboard : _cardsAboardStarships) {
                // Get any "change seat" actions
                Action moveBetweenCapacitySlotsAction = cardAboard.getBlueprint().getMoveBetweenCapacitySlotsAction(_playerId, game, cardAboard);
                if (moveBetweenCapacitySlotsAction != null) {
                    actions.add(moveBetweenCapacitySlotsAction);
                }

                // Get any "move between ships" actions
                Action moveBetweenStarshipsAction = cardAboard.getBlueprint().getMoveBetweenDockedStarshipsAction(_playerId, game, cardAboard);
                if (moveBetweenStarshipsAction != null) {
                    actions.add(moveBetweenStarshipsAction);
                }
            }

            if (!actions.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new CardActionSelectionDecision(1, "Perform a ship-docked action or Pass", actions, _playerId.equals(game.getGameState().getCurrentPlayerId()), false, false, false, false) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                Action action = getSelectedAction(result);
                                if (action != null) {
                                    game.getActionsEnvironment().addActionToStack(action);
                                    _subAction.insertEffect(
                                            new ChooseNextAction(_subAction));
                                }
                            }
                        });
            }
        }
    }
}
