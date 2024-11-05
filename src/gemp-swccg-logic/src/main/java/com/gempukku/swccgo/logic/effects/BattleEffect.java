package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.actions.battle.BattleDamageSegmentAction;
import com.gempukku.swccgo.logic.timing.actions.battle.BattlePowerSegmentAction;
import com.gempukku.swccgo.logic.timing.actions.battle.BattleWeaponsSegmentAction;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndingResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;
import com.gempukku.swccgo.logic.timing.results.BattleWeaponsSegmentCompletedResult;

import java.util.Collection;

/**
 * An effect that performs a battle.
 */
public class BattleEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;
    private boolean _isLocalTrouble;
    private Collection<PhysicalCard> _localTroubleParticipants;
    private Collection<Modifier> _extraModifiers;

    /**
     * Creates an effect that performs a battle at a location.
     * @param action the action performing this effect
     * @param location the battle location
     * @param isLocalTrouble true if battle is a Local Trouble battle, otherwise false
     * @param localTroubleParticipants the Local Trouble battle participants, or null if not a Local Trouble battle
     */
    public BattleEffect(Action action, PhysicalCard location, boolean isLocalTrouble, final Collection<PhysicalCard> localTroubleParticipants, Collection<Modifier> extraModifiers) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
        _isLocalTrouble = isLocalTrouble;
        _localTroubleParticipants = localTroubleParticipants;
        _extraModifiers = extraModifiers;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // 1) Record that battle is initiated (and cards involved)
        subAction.appendEffect(
                new RecordBattleInitiatedEffect(subAction, _location, _isLocalTrouble, _localTroubleParticipants, _extraModifiers));

        // 2) Battle just initiated
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new BattleInitiatedResult(subAction, _location)));

        // 3) Weapons segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            SubAction subAction = (SubAction) getAction();
                            // Perform weapons segment
                            subAction.stackSubAction(
                                    new BattleWeaponsSegmentAction(game));
                        }
                    }
                }
        );

        // 4) Weapons segment just setFulfilledByOtherAction
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            game.getActionsEnvironment().emitEffectResult(new BattleWeaponsSegmentCompletedResult(_performingPlayerId));
                        }
                    }
                }
        );

        // 5) Power segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            SubAction subAction = (SubAction) getAction();
                            subAction.stackSubAction(
                                    new BattlePowerSegmentAction());
                        }
                    }
                }
        );

        // 6) Determine winner
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            SubAction subAction = (SubAction) getAction();
                            subAction.insertEffect(
                                    new DetermineBattleWinnerEffect(subAction));
                        }
                    }
                }
        );

        // 7) Damage segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            SubAction subAction = (SubAction) getAction();
                            subAction.stackSubAction(
                                    new BattleDamageSegmentAction(game));
                        }
                    }
                }
        );

        // 8) Battle ending
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if battle can continue
                        if (gameState.getBattleState() != null && gameState.getBattleState().canContinue(game)) {
                            SubAction subAction = (SubAction) getAction();
                            game.getActionsEnvironment().emitEffectResult(new BattleEndingResult(subAction, _location));
                        }
                    }
                }
        );

        // 9) Battle ends
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        if (gameState.getBattleState() != null && !gameState.getBattleState().isCanceled()) {
                            String msgText = (_isLocalTrouble ? "Local Trouble battle" : "Battle") + " at " + GameUtils.getCardLink(_location) + " ends";
                            game.getGameState().sendMessage(msgText);
                            SubAction subAction = (SubAction) getAction();
                            game.getActionsEnvironment().emitEffectResult(new BattleEndedResult(subAction, _location, gameState.getBattleState()));
                        }
                        game.getGameState().endBattle();
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
