package com.gempukku.swccgo.logic.timing.actions.attack;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DrawFerocityDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/**
 * An action that carries out the power segment of an attack.
 */
public class AttackPowerSegmentAction extends SystemQueueAction {

    /**
     * Creates an action that carries out the power segment of an attack.
     */
    public AttackPowerSegmentAction() {
        appendEffect(
                new PassthruEffect(this) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        AttackState attackState  = game.getGameState().getAttackState();

                        // Perform power segment
                        attackState.reachedPowerSegment();

                        // Check if parasite (automatically wins)
                        if (attackState.isParasiteAttackingNonCreature()) {
                            game.getGameState().sendMessage(GameUtils.getAppendedNames(attackState.getCardsAttacking()) + "'s attack against " + GameUtils.getAppendedNames(attackState.getCardsDefending()) + " is successful");
                        }
                        else {
                            // Draw attack or ferocity destinies
                            appendEffect(
                                    new DrawAttackOrFerocityDestiny(_action, false));
                            appendEffect(
                                    new DrawAttackOrFerocityDestiny(_action, true));
                        }
                    }
                }
        );
    }

    /**
     * A private effect that causes the appropriate player to draw attack or ferocity destiny.
     */
    private class DrawAttackOrFerocityDestiny extends AbstractSubActionEffect {
        private boolean _defender;

        /**
         * Creates a private effect that causes the appropriate player to draw attack or ferocity destiny.
         * @param action the action performing this effect
         * @param defender true if drawing destiny for defender, otherwise drawing for attacker
         */
        private DrawAttackOrFerocityDestiny(Action action, boolean defender) {
            super(action);
            _defender = defender;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final AttackState attackState = gameState.getAttackState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final String performingPlayerId = _defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner();

            final SubAction subAction = new SubAction(_action, performingPlayerId);

            // Determine if drawing ferocity destiny or attack destiny
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (attackState.isCreaturesAttackingEachOther()
                                    || (_defender && attackState.isNonCreatureAttackingCreature())
                                    || (!_defender && attackState.isCreatureAttackingNonCreature())) {

                                final PhysicalCard creature = _defender ? attackState.getCardsDefending().iterator().next() : attackState.getCardsAttacking().iterator().next();
                                subAction.appendEffect(
                                        new DrawFerocityDestinyEffect(subAction, creature) {
                                            @Override
                                            protected void totalFerocityDestinyCalculated(Float totalFerocityDestiny) {
                                                attackState.setFerocityDestinyTotal(creature, totalFerocityDestiny != null ? totalFerocityDestiny : 0);
                                            }
                                        });
                            }
                            else {
                                final int numDraws = modifiersQuerying.getNumAttackDestinyDraws(gameState, performingPlayerId, false, false);
                                if (numDraws > 0) {
                                    if (gameState.getReserveDeckSize(performingPlayerId) > 0) {

                                        // Ask player if they want to draw destiny
                                        game.getUserFeedback().sendAwaitingDecision(performingPlayerId,
                                                new YesNoDecision("Do you want to draw " + numDraws + " destiny?") {
                                                    @Override
                                                    protected void yes() {
                                                        subAction.appendEffect(
                                                                new DrawDestinyEffect(_action, performingPlayerId, numDraws) {
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        attackState.setAttackDestinyTotal(performingPlayerId, totalDestiny != null ? totalDestiny : 0);
                                                                    }
                                                                });
                                                    }
                                                });
                                    } else {
                                        gameState.sendMessage(performingPlayerId + " can't draw destiny. No more cards in Reserve Deck");
                                        attackState.setAttackDestinyTotal(performingPlayerId, 0);
                                    }
                                }
                            }
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
}
