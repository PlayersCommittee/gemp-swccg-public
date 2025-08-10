package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LightsaberCombatAddOrModifyLightsaberCombatDestiniesStepResult;
import com.gempukku.swccgo.logic.timing.results.LightsaberCombatEndedResult;
import com.gempukku.swccgo.logic.timing.results.LightsaberCombatInitiatedResult;
import com.gempukku.swccgo.logic.timing.results.LightsaberCombatResultDeterminedResult;


/**
 * An effect that performs lightsaber combat.
 */
public class LightsaberCombatEffect extends AbstractSubActionEffect {
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private LightsaberCombatDirections _lightsaberCombatDirections;

    /**
     * Creates an effect that performs lightsaber combat at a location.
     * @param action the action performing this effect
     * @param darkSideCharacter the dark side character to participate in lightsaber combat
     * @param lightSideCharacter the light side character to participate in lightsaber combat
     * @param lightsaberCombatDirections the lightsaber combat directions
     */
    public LightsaberCombatEffect(Action action, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, LightsaberCombatDirections lightsaberCombatDirections) {
        super(action);
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _lightsaberCombatDirections = lightsaberCombatDirections;
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

        // 1) Record that light combat is initiated (and cards involved)
        subAction.appendEffect(
                new RecordLightsaberCombatInitiatedEffect(subAction, location, _darkSideCharacter, _lightSideCharacter, _lightsaberCombatDirections));

        // 2) Lightsaber combat just initiated
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new LightsaberCombatInitiatedResult(subAction)));

        // 3) Add or modify lightsaber combat destinies step
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if lightsaber combat can continue
                        if (gameState.getLightsaberCombatState().canContinue(game)) {

                            actionsEnvironment.emitEffectResult(new LightsaberCombatAddOrModifyLightsaberCombatDestiniesStepResult(subAction));
                        }
                    }
                }
        );

        // 4) Continue with performing lightsaber combat actions from the card initiating lightsaber combat.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if lightsaber combat can continue
                        if (gameState.getLightsaberCombatState().canContinue(game)) {

                            final SubAction lightsaberCombatCardEffectAction = new SubAction(subAction);
                            lightsaberCombatCardEffectAction.appendEffect(
                                    new PassthruEffect(lightsaberCombatCardEffectAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            _lightsaberCombatDirections.performLightsaberCombatDirections(lightsaberCombatCardEffectAction, game, gameState.getLightsaberCombatState());
                                        }
                                    }
                            );
                            // Stack sub-action
                            subAction.stackSubAction(lightsaberCombatCardEffectAction);
                        }
                    }
                }
        );

        // 5) Set final lightsaber combat totals
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
                        // Check if lightsaber combat can continue
                        if (lightsaberCombatState.canContinue(game)) {
                            String darkSidePlayer = game.getDarkPlayer();
                            String lightSidePlayer = game.getLightPlayer();
                            lightsaberCombatState.setFinalLightsaberCombatTotal(darkSidePlayer, modifiersQuerying.getLightsaberCombatTotal(gameState, darkSidePlayer));
                            lightsaberCombatState.setFinalLightsaberCombatTotal(lightSidePlayer, modifiersQuerying.getLightsaberCombatTotal(gameState, lightSidePlayer));
                            lightsaberCombatState.reachedResults();

                            // Send messages about result
                            gameState.sendMessage(darkSidePlayer + "'s final lightsaber combat total: " + GuiUtils.formatAsString(lightsaberCombatState.getFinalLightsaberCombatTotal(darkSidePlayer)));
                            gameState.sendMessage(lightSidePlayer + "'s final lightsaber combat total: " + GuiUtils.formatAsString(lightsaberCombatState.getFinalLightsaberCombatTotal(lightSidePlayer)));
                            PhysicalCard winner = lightsaberCombatState.getWinningCharacter();
                            if (winner != null) {
                                gameState.sendMessage(GameUtils.getCardLink(winner) + " wins lightsaber combat against " + GameUtils.getCardLink(lightsaberCombatState.getLosingCharacter()));
                            }
                            else {
                                gameState.sendMessage("Lightsaber combat results in a tie");
                            }

                            actionsEnvironment.emitEffectResult(new LightsaberCombatResultDeterminedResult(subAction, lightsaberCombatState.getWinner(), lightsaberCombatState.getWinningCharacter(), lightsaberCombatState.getLoser(), lightsaberCombatState.getLosingCharacter()));
                        }
                    }
                }
        );

        // 6) Continue with performing lightsaber combat results from the card initiating lightsaber combat.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if lightsaber combat can continue
                        if (gameState.getLightsaberCombatState().canContinue(game)) {
                            final SubAction lightsaberCombatCardEffectAction = new SubAction(subAction);
                            lightsaberCombatCardEffectAction.appendEffect(
                                    new PassthruEffect(lightsaberCombatCardEffectAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            _lightsaberCombatDirections.performLightsaberCombatResults(lightsaberCombatCardEffectAction, game, gameState.getLightsaberCombatState());
                                        }
                                    }
                            );
                            // Stack sub-action
                            subAction.stackSubAction(lightsaberCombatCardEffectAction);
                        }
                    }
                }
        );

        // 7) Lightsaber combat ends
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
                        if (!lightsaberCombatState.isCanceled()) {
                            lightsaberCombatState.resultsComplete();

                            String msgText = "Lightsaber combat between " + GameUtils.getCardLink(_darkSideCharacter) + " and " + GameUtils.getCardLink(_lightSideCharacter) + " ends";
                            game.getGameState().sendMessage(msgText);

                            actionsEnvironment.emitEffectResult(new LightsaberCombatEndedResult(subAction, lightsaberCombatState.getWinner(), lightsaberCombatState.getWinningCharacter(), lightsaberCombatState.getLoser(), lightsaberCombatState.getLosingCharacter()));
                        }
                        game.getGameState().endLightsaberCombat();
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
