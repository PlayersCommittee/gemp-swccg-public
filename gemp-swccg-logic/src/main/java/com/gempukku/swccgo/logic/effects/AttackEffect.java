package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.actions.attack.AttackDamageSegmentAction;
import com.gempukku.swccgo.logic.timing.actions.attack.AttackPowerSegmentAction;
import com.gempukku.swccgo.logic.timing.actions.attack.AttackWeaponsSegmentAction;
import com.gempukku.swccgo.logic.timing.results.AttackEndedResult;
import com.gempukku.swccgo.logic.timing.results.AttackInitiatedResult;

/**
 * An effect that performs an attack.
 */
public class AttackEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardBeingAttacked;
    private PhysicalCard _creatureAttacking;

    /**
     * Creates an effect that performs an attack on a creature.
     * @param action the action performing this effect
     * @param creatureBeingAttacked the creature being attacked
     */
    public AttackEffect(Action action, PhysicalCard creatureBeingAttacked) {
        super(action);
        _cardBeingAttacked = creatureBeingAttacked;
    }

    /**
     * Creates an effect that performs an attack by a creature.
     * @param action the action performing this effect
     * @param cardBeingAttacked the card being attacked
     * @param creatureAttacking the creature attacking
     */
    public AttackEffect(Action action, PhysicalCard cardBeingAttacked, PhysicalCard creatureAttacking) {
        super(action);
        _cardBeingAttacked = cardBeingAttacked;
        _creatureAttacking = creatureAttacking;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // 1) Record that attack is initiated (and cards involved)
        subAction.appendEffect(
                new RecordAttackInitiatedEffect(subAction, _cardBeingAttacked, _creatureAttacking));

        // 2) Attack just initiated
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new AttackInitiatedResult(subAction)));

        // 3) Weapons segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if attack can continue
                        if (gameState.getAttackState() != null && gameState.getAttackState().canContinue()
                                && !gameState.getAttackState().isCreaturesAttackingEachOther()) {
                            SubAction subAction = (SubAction) getAction();
                            // Perform weapons segment
                            subAction.stackSubAction(
                                    new AttackWeaponsSegmentAction(game));
                        }
                    }
                }
        );

        // 4) Power segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if attack can continue
                        if (gameState.getAttackState() != null && gameState.getAttackState().canContinue()) {
                            SubAction subAction = (SubAction) getAction();
                            subAction.stackSubAction(
                                    new AttackPowerSegmentAction());
                        }
                    }
                }
        );

        // 5) Set final attack totals and determine defeated cards
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        AttackState attackState = gameState.getAttackState();
                        // Check if attack can continue
                        if (attackState.canContinue()) {
                            if (!attackState.isParasiteAttackingNonCreature()) {
                                float attackerTotal = modifiersQuerying.getAttackTotal(gameState, false);
                                float defenderTotal = modifiersQuerying.getAttackTotal(gameState, true);
                                attackState.setFinalAttackerTotal(attackerTotal);
                                attackState.setFinalDefenderTotal(defenderTotal);

                                // Send messages about result
                                if (attackState.isCreaturesAttackingEachOther()) {
                                    gameState.sendMessage("Attack total for " + GameUtils.getAppendedNames(attackState.getCardsAttacking()) + ": " + GuiUtils.formatAsString(attackerTotal));
                                    gameState.sendMessage("Attack total for " + GameUtils.getAppendedNames(attackState.getCardsDefending()) + ": " + GuiUtils.formatAsString(defenderTotal));
                                    if (attackerTotal > defenderTotal) {
                                        gameState.sendMessage(GameUtils.getAppendedNames(attackState.getCardsAttacking()) + " defeats " + GameUtils.getAppendedNames(attackState.getCardsDefending()));
                                        attackState.defenderDefeated();
                                    } else if (defenderTotal > attackerTotal) {
                                        gameState.sendMessage(GameUtils.getAppendedNames(attackState.getCardsDefending()) + " defeats " + GameUtils.getAppendedNames(attackState.getCardsAttacking()));
                                        attackState.attackingCreatureDefeated();
                                    } else {
                                        gameState.sendMessage(GameUtils.getAppendedNames(attackState.getCardsAttacking()) + " and " + GameUtils.getAppendedNames(attackState.getCardsDefending()) + " defeat each other");
                                        attackState.attackingCreatureDefeated();
                                        attackState.defenderDefeated();
                                    }
                                } else {
                                    gameState.sendMessage("Attacker total: " + GuiUtils.formatAsString(attackerTotal));
                                    gameState.sendMessage("Defender total: " + GuiUtils.formatAsString(defenderTotal));
                                    if (attackerTotal > defenderTotal) {
                                        gameState.sendMessage(GameUtils.getAppendedNames(attackState.getCardsDefending()) + " is defeated");
                                        attackState.defenderDefeated();
                                    } else {
                                        gameState.sendMessage(GameUtils.getAppendedNames(attackState.getCardsDefending()) + " is not defeated");
                                    }
                                }
                            }
                        }
                    }
                }
        );

        // 6) Damage segment
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        // Check if attack can continue
                        if (gameState.getAttackState() != null && gameState.getAttackState().canContinue()) {
                            SubAction subAction = (SubAction) getAction();
                            subAction.stackSubAction(
                                    new AttackDamageSegmentAction(game));
                        }
                    }
                }
        );

        // 7) Attack ends
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        if (gameState.getAttackState() != null && !gameState.getAttackState().isCanceled()) {
                            String msgText = "Attack ends";
                            game.getGameState().sendMessage(msgText);
                            SubAction subAction = (SubAction) getAction();
                            game.getActionsEnvironment().emitEffectResult(new AttackEndedResult(subAction));
                        }
                        game.getGameState().endAttack();
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
