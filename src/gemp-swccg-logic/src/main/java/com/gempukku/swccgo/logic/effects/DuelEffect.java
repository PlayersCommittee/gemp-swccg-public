package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DuelAddOrModifyDuelDestiniesStepResult;
import com.gempukku.swccgo.logic.timing.results.DuelEndingResult;
import com.gempukku.swccgo.logic.timing.results.DuelInitiatedResult;
import com.gempukku.swccgo.logic.timing.results.DuelResultDeterminedResult;


/**
 * An effect that performs a duel.
 */
public class DuelEffect extends AbstractSubActionEffect {
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private DuelDirections _duelDirections;

    /**
     * Creates an effect that performs a duel at a location.
     * @param action the action performing this effect
     * @param darkSideCharacter the dark side character to duel
     * @param lightSideCharacter the light side character to duel
     * @param duelDirections the duel directions
     */
    public DuelEffect(Action action, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, DuelDirections duelDirections) {
        super(action);
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _duelDirections = duelDirections;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, _darkSideCharacter);

        final SubAction subAction = new SubAction(_action);

        // 1) Record that duel is initiated (and cards involved)
        subAction.appendEffect(
                new RecordDuelInitiatedEffect(subAction, location, _darkSideCharacter, _lightSideCharacter, _duelDirections));

        // 2) Duel just initiated
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new DuelInitiatedResult(subAction, _duelDirections.isEpicDuel())));

        // 3) Add or modify duel destinies step
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if duel can continue
                        if (gameState.getDuelState().canContinue(game)) {

                            actionsEnvironment.emitEffectResult(new DuelAddOrModifyDuelDestiniesStepResult(subAction));
                        }
                    }
                }
        );

        // 4) Continue with performing duel actions from the card initiating the duel.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if duel can continue
                        if (gameState.getDuelState().canContinue(game)) {

                            final SubAction duelCardEffectAction = new SubAction(subAction);
                            duelCardEffectAction.appendEffect(
                                    new PassthruEffect(duelCardEffectAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            _duelDirections.performDuelDirections(duelCardEffectAction, game, gameState.getDuelState());
                                        }
                                    }
                            );
                            // Stack sub-action
                            subAction.stackSubAction(duelCardEffectAction);
                        }
                    }
                }
        );

        // 5) Set final duel totals and determine winner/loser
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        DuelState duelState = gameState.getDuelState();
                        // Check if duel can continue
                        if (duelState.canContinue(game)) {
                            String darkSidePlayer = game.getDarkPlayer();
                            String lightSidePlayer = game.getLightPlayer();
                            duelState.setFinalDuelTotal(darkSidePlayer, modifiersQuerying.getDuelTotal(gameState, darkSidePlayer));
                            duelState.setFinalDuelTotal(lightSidePlayer, modifiersQuerying.getDuelTotal(gameState, lightSidePlayer));
                            duelState.reachedResults();

                            // Send messages about result
                            gameState.sendMessage(darkSidePlayer + "'s final duel total: " + GuiUtils.formatAsString(duelState.getFinalDuelTotal(darkSidePlayer)));
                            gameState.sendMessage(lightSidePlayer + "'s final duel total: " + GuiUtils.formatAsString(duelState.getFinalDuelTotal(lightSidePlayer)));
                            PhysicalCard winner = duelState.getWinningCharacter();
                            if (winner != null) {
                                gameState.sendMessage(GameUtils.getCardLink(winner) + " wins " +
                                        (_duelDirections.isEpicDuel() ? "epic " : "") + "duel against " + GameUtils.getCardLink(duelState.getLosingCharacter()));
                            }
                            else {
                                gameState.sendMessage((_duelDirections.isEpicDuel() ? "Epic duel" : "Duel") + " results in a tie");
                            }

                            actionsEnvironment.emitEffectResult(new DuelResultDeterminedResult(subAction, duelState.getWinner(), duelState.getWinningCharacter(), duelState.getLoser(), duelState.getLosingCharacter()));
                        }
                    }
                }
        );

        // 6) Continue with performing duel results from the card initiating the duel
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if duel can continue
                        if (gameState.getDuelState().canContinue(game)) {
                            final SubAction duelCardEffectAction = new SubAction(subAction);
                            duelCardEffectAction.appendEffect(
                                    new PassthruEffect(duelCardEffectAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            _duelDirections.performDuelResults(duelCardEffectAction, game, gameState.getDuelState());
                                        }
                                    }
                            );
                            // Stack sub-action
                            subAction.stackSubAction(duelCardEffectAction);
                        }
                    }
                }
        );

        // 7) Trigger responses to duel ending
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        DuelState duelState = gameState.getDuelState();
                        if (!duelState.isCanceled()) {
                            duelState.resultsComplete();

                            actionsEnvironment.emitEffectResult(new DuelEndingResult(subAction, duelState.getWinner(), duelState.getWinningCharacter(), duelState.getLoser(), duelState.getLosingCharacter()));
                        }
                    }
                }
        );

        // 8) Duel ended
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        DuelState duelState = gameState.getDuelState();
                        if (!duelState.isCanceled()) {

                            String msgText = (_duelDirections.isEpicDuel() ? "Epic duel" : "Duel") + " between " + GameUtils.getCardLink(_darkSideCharacter) + " and " + GameUtils.getCardLink(_lightSideCharacter) + " ends";
                            gameState.sendMessage(msgText);
                        }
                        gameState.endDuel();
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
