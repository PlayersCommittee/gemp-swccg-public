package com.gempukku.swccgo.logic.timing.actions.attack;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.AttachParasiteEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultsEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An action that carries out the damage segment of an attack.
 */
public class AttackDamageSegmentAction extends SystemQueueAction {

    /**
     * Creates an action that carries out the damage segment of an attack.
     * @param game the game
     */
    public AttackDamageSegmentAction(SwccgGame game) {
        final Action that = this;
        appendEffect(
                new PassthruEffect(that) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
                        final AttackState attackState = gameState.getAttackState();
                        attackState.reachedDamageSegment();
                        final PhysicalCard attackingCard = attackState.getCardsAttacking().iterator().next();
                        final PhysicalCard defendingCard = attackState.getCardsDefending().iterator().next();

                        // If parasite attack, then attach parasite to host
                        if (attackState.isParasiteAttackingNonCreature()) {
                            appendEffect(
                                    new AttachParasiteEffect(that, attackingCard, defendingCard));
                            // Make any 'hit' creatures lost
                            appendEffect(
                                    new PassthruEffect(that) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            Collection<PhysicalCard> hitCreatures = Filters.filter(attackState.getAllCardsParticipating(), game, Filters.and(Filters.creature, Filters.hit));
                                            if (!hitCreatures.isEmpty()) {
                                                appendEffect(
                                                        new LoseCardsFromTableEffect(that, hitCreatures));
                                            }
                                        }
                                    }
                            );
                            return;
                        }

                        // Emit defeated results
                        if (attackState.isAttackingCreatureDefeated()) {
                            actionsEnvironment.emitEffectResult(
                                    new DefeatedResult(attackingCard, attackState.getCardsDefending(), attackState.getPlayerInitiatedAttack()));
                        }
                        if (attackState.isDefenderDefeated()) {
                            actionsEnvironment.emitEffectResult(
                                    new DefeatedResult(defendingCard, attackState.getCardsAttacking(), attackState.getPlayerInitiatedAttack()));
                        }

                        // Determine what to do with defeated cards still at attack location
                        appendEffect(
                                new PassthruEffect(that) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        final List<PhysicalCard> cardsToBeLost = new ArrayList<>();
                                        List<PhysicalCard> cardsToBePlacedOutOfPlay = new ArrayList<>();
                                        List<EffectResult> effectResults = new ArrayList<>();

                                        // Determine if defeated cards are 'eaten' (lost, placed out of play, etc.)
                                        if (attackState.isCreaturesAttackingEachOther()) {
                                            if (!attackState.isDefenderDefeated()) {
                                                if (Filters.at(Filters.attackLocation).accepts(game, attackingCard)) {
                                                    // Defeated card is 'eaten'
                                                    float ferocity = modifiersQuerying.getAttackTotalPowerOrFerocity(gameState, false);
                                                    float forfeitValue = modifiersQuerying.getForfeit(gameState, attackingCard);
                                                    effectResults.add(
                                                            new EatenResult(attackingCard, null, ferocity, forfeitValue, false, defendingCard, attackState.getAttackLocation()));
                                                    if (Filters.placedOutOfPlayWhenEatenBy(defendingCard).accepts(game, attackingCard)) {
                                                        cardsToBePlacedOutOfPlay.add(attackingCard);
                                                    }
                                                    else {
                                                        cardsToBeLost.add(attackingCard);
                                                    }
                                                }
                                            }
                                            else if (!attackState.isAttackingCreatureDefeated()) {
                                                if (Filters.at(Filters.attackLocation).accepts(game, defendingCard)) {
                                                    // Defeated card is 'eaten'
                                                    float ferocity = modifiersQuerying.getAttackTotalPowerOrFerocity(gameState, true);
                                                    float forfeitValue = modifiersQuerying.getForfeit(gameState, defendingCard);
                                                    effectResults.add(
                                                            new EatenResult(defendingCard, null, ferocity, forfeitValue, false, attackingCard, attackState.getAttackLocation()));
                                                    if (Filters.placedOutOfPlayWhenEatenBy(attackingCard).accepts(game, defendingCard)) {
                                                        cardsToBePlacedOutOfPlay.add(defendingCard);
                                                    }
                                                    else {
                                                        cardsToBeLost.add(defendingCard);
                                                    }
                                                }
                                            }
                                            else {
                                                if (Filters.at(Filters.attackLocation).accepts(game, attackingCard)) {
                                                    cardsToBeLost.add(attackingCard);
                                                }
                                                if (Filters.at(Filters.attackLocation).accepts(game, defendingCard)) {
                                                    cardsToBeLost.add(defendingCard);
                                                }
                                            }
                                        }
                                        else if (attackState.isDefenderDefeated()) {
                                            if (attackState.isCreatureAttackingNonCreature()) {
                                                if (Filters.at(Filters.attackLocation).accepts(game, defendingCard)) {
                                                    // Defeated card is 'eaten'
                                                    float power = modifiersQuerying.getPower(gameState, defendingCard);
                                                    float forfeitValue = modifiersQuerying.getForfeit(gameState, defendingCard);
                                                    boolean captive = defendingCard.isCaptive();
                                                    effectResults.add(
                                                            new EatenResult(defendingCard, power, null, forfeitValue, captive, attackingCard, attackState.getAttackLocation()));
                                                    if (Filters.placedOutOfPlayWhenEatenBy(attackingCard).accepts(game, defendingCard)) {
                                                        cardsToBePlacedOutOfPlay.add(defendingCard);
                                                    }
                                                    else {
                                                        cardsToBeLost.add(defendingCard);
                                                    }
                                                }
                                            }
                                            else {
                                                if (Filters.at(Filters.attackLocation).accepts(game, defendingCard)) {
                                                    cardsToBeLost.add(defendingCard);
                                                }
                                            }
                                        }

                                        // Emit 'eaten' results
                                        final boolean eaten = !effectResults.isEmpty();
                                        appendEffect(
                                                new TriggeringResultsEffect(that, effectResults));

                                        // Cause defeated cards still at attack location to be lost or placed out of play
                                        if (!cardsToBeLost.isEmpty()) {
                                            appendEffect(
                                                    new LoseCardsFromTableEffect(that, cardsToBeLost) {
                                                        @Override
                                                        protected boolean asEaten() {
                                                            return eaten;
                                                        }
                                                    });
                                        }
                                        if (!cardsToBePlacedOutOfPlay.isEmpty()) {
                                            appendEffect(
                                                    new PlaceCardsOutOfPlayFromTableEffect(that, cardsToBePlacedOutOfPlay) {
                                                        @Override
                                                        protected boolean asEaten() {
                                                            return eaten;
                                                        }
                                                    });
                                        }

                                        // Make any 'hit' creatures lost
                                        appendEffect(
                                                new PassthruEffect(that) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        Collection<PhysicalCard> hitCreatures = Filters.filter(attackState.getAllCardsParticipating(), game, Filters.and(Filters.creature, Filters.hit));
                                                        if (!hitCreatures.isEmpty()) {
                                                            appendEffect(
                                                                    new LoseCardsFromTableEffect(that, hitCreatures));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                        }
                }
        );
    }
}
