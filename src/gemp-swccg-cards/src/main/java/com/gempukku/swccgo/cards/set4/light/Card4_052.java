package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect;
import com.gempukku.swccgo.logic.effects.ChooseTargetGroupToRetargetEffectFromToSameSideOfForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.RetargetCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.RetargetEffectEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForRetargetingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect;
import com.gempukku.swccgo.logic.effects.TargetTargetOnSameSideOfForceToRetargetEffectToEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: I Have A Bad Feeling About This
 */
public class Card4_052 extends AbstractLostInterrupt {
    public Card4_052() {
        super(Side.LIGHT, 3, "I Have A Bad Feeling About This", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("'This grounds sure feels strange. Doesn't feel like rock. I don't know...'");
        setGameText("Use 3 Force: Retarget an Interrupt or Utinni Effect which specifies a target to another appropriate target on the same side of the Force. OR Relocate any Effect (except those immune to Alter) deployed on a location to another appropriate location.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, PhysicalCard self) {
        Filter filter = Filters.and(Filters.or(Filters.Interrupt, Filters.Utinni_Effect), Filters.cardBeingPlayedCanBeRetargetedToSameSideOfForce(self));

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canRetargetCardBeingPlayed(game, self, effect)) {
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Retarget " + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForRetargetingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeRetargeted(final PhysicalCard cardBeingPlayed) {
                            // Choose target to re-target from
                            action.appendTargeting(
                                    new ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect(action, respondableEffect) {
                                        @Override
                                        public void groupChosenToRetarget(final int groupIdToRetarget) {
                                            // Choose target to re-target to
                                            action.appendTargeting(
                                                    new TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect(action, respondableEffect, groupIdToRetarget) {
                                                        @Override
                                                        public void cardsRetargetedTo(final int targetGroupId1, Collection<PhysicalCard> targetedCards) {
                                                            action.addAnimationGroup(targetedCards);
                                                            Collection<PhysicalCard> cardsToTargetFrom = ((RespondablePlayingCardEffect) effect).getTargetingAction().getPrimaryTargetCards(groupIdToRetarget);
                                                            // Pay cost(s)
                                                            action.appendCost(
                                                                    new UseForceEffect(action, playerId, 3));
                                                            // Allow response(s)
                                                            action.allowResponses("Re-target " + GameUtils.getCardLink(cardBeingPlayed) + " from " + GameUtils.getAppendedNames(cardsToTargetFrom) + " to " + GameUtils.getAppendedNames(targetedCards),
                                                                    new RespondablePlayCardEffect(action) {
                                                                        @Override
                                                                        protected void performActionResults(Action targetingAction) {
                                                                            // Get the targeted card(s) from the action using the targetGroupId
                                                                            Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId1);

                                                                            // Perform result(s)
                                                                            action.appendEffect(
                                                                                    new RetargetCardBeingPlayedEffect(action, respondableEffect, groupIdToRetarget, finalTargets));
                                                                        }
                                                                    });
                                                        }
                                                    }
                                            );
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            Filter utinniEffectFilter = Filters.and(Filters.Utinni_Effect, Filters.effectCanBeRetargetedToSameSideOfForce(self));

            if (GameConditions.canTarget(game, self, utinniEffectFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Re-target Utinni Effect");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Utinni Effect to re-target", utinniEffectFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEffect) {
                                action.addAnimationGroup(targetedEffect);
                                // Choose target to re-target from
                                action.appendTargeting(
                                        new ChooseTargetGroupToRetargetEffectFromToSameSideOfForceEffect(action, targetedEffect) {
                                            @Override
                                            public void targetToRetarget(final TargetId targetIdToRetarget) {
                                                // Choose target to re-target to
                                                action.appendTargeting(
                                                        new TargetTargetOnSameSideOfForceToRetargetEffectToEffect(action, targetedEffect, targetIdToRetarget) {
                                                            @Override
                                                            public void cardRetargetedTo(final int targetGroupId2, PhysicalCard targetedTarget) {
                                                                action.addAnimationGroup(targetedTarget);
                                                                PhysicalCard cardToTargetFrom = targetedEffect.getTargetedCard(game.getGameState(), targetIdToRetarget);
                                                                // Set target filters for re-targeting
                                                                action.updatePrimaryTargetFilter(targetGroupId1, Filters.and(Filters.Utinni_Effect, Filters.effectCanBeRetargetedTo(self, Filters.inActionTargetGroup(action, targetGroupId2))));
                                                                action.updatePrimaryTargetFilter(targetGroupId2, Filters.cardThatEffectCanBeRetargetedTo(self, Filters.inActionTargetGroup(action, targetGroupId1)));
                                                                // Pay cost(s)
                                                                action.appendCost(
                                                                        new UseForceEffect(action, playerId, 3));
                                                                // Allow response(s)
                                                                action.allowResponses("Re-target " + GameUtils.getCardLink(targetedEffect) + " from " + GameUtils.getCardLink(cardToTargetFrom) + " to " + GameUtils.getCardLink(targetedTarget),
                                                                        new RespondablePlayCardEffect(action) {
                                                                            @Override
                                                                            protected void performActionResults(Action targetingAction) {
                                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                                PhysicalCard finalEffect = action.getPrimaryTargetCard(targetGroupId1);
                                                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId2);

                                                                                // Perform result(s)
                                                                                action.appendEffect(
                                                                                        new RetargetEffectEffect(action, finalEffect, finalTarget));
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }

            Filter effectFilter = Filters.and(Filters.Effect, Filters.except(Filters.immune_to_Alter), Filters.attachedTo(Filters.location),
                    Filters.effectCanBeRelocatedTo(playerId, Filters.and(Filters.location, Filters.canBeTargetedBy(self))));

            if (GameConditions.canTarget(game, self, effectFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate Effect");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Effect to relocate", effectFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEffect) {
                                action.addAnimationGroup(targetedEffect);
                                Filter locationFilter = Filters.and(Filters.location, Filters.not(Filters.hasAttached(targetedEffect)), Filters.canRelocateEffectTo(playerId, targetedEffect));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose location to relocated Effect to", locationFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedLocation) {
                                                action.addAnimationGroup(targetedLocation);
                                                // Set target filters for re-targeting
                                                action.updatePrimaryTargetFilter(targetGroupId1, Filters.and(Filters.Effect, Filters.except(Filters.immune_to_Alter), Filters.attachedTo(Filters.location),
                                                        Filters.effectCanBeRelocatedTo(playerId, Filters.and(Filters.inActionTargetGroup(action, targetGroupId2), Filters.canBeTargetedBy(self)))));
                                                action.updatePrimaryTargetFilter(targetGroupId2, Filters.and(Filters.location, Filters.canRelocateEffectTo(playerId, Filters.inActionTargetGroup(action, targetGroupId1))));
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new UseForceEffect(action, playerId, 3));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedEffect) + " to " + GameUtils.getCardLink(targetedLocation),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                PhysicalCard finalEffect = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AttachCardFromTableEffect(action, finalEffect, finalLocation));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        return actions;
    }
}