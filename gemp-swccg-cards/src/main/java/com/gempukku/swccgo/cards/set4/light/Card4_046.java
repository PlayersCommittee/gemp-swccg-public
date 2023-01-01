package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DetachParasitesEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AttackTargetSelectedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Blasted Varmints
 */
public class Card4_046 extends AbstractUsedInterrupt {
    public Card4_046() {
        super(Side.LIGHT, 5, "Blasted Varmints", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'I just got this bucket back together. I'm not gonna let something tear it apart.'");
        setGameText("Use 1 Force to cause all Mynocks or Vine Snakes to detach from one starfighter or character. OR Cause one Mynock attached to any starfighter to be lost. OR After creature has selected a target character, cancel that selection and select again.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (effectResult.getType() == EffectResult.Type.ATTACK_TARGET_SELECTED) {

            final InitiateAttackNonCreatureAction creatureAction = ((AttackTargetSelectedResult)effectResult).getInitiateAttackNonCreatureAction();

            if (Filters.character.accepts(game,((AttackTargetSelectedResult)effectResult).getTarget())
                    && !creatureAction.isTargetChanged()) {
                //retarget it
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reselect creature target");
                action.setActionMsg("Cancel creature target selection and select again");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                creatureAction.resetTargetSelection();
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        final Filter vineSnakeOrMynock = Filters.or(Filters.title("Vine Snake"), Filters.title("Mynock"));
        Filter filter = Filters.and(Filters.or(Filters.creature, Filters.starfighter), Filters.hasAttached(vineSnakeOrMynock));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Detach creatures from card");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a character or starfighter", filter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.appendCost(new UseForceEffect(action, playerId, 1));
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                            Collection<PhysicalCard> toDetach = Filters.filterAllOnTable(game, Filters.and(vineSnakeOrMynock, Filters.attachedTo(finalTarget), Filters.canBeTargetedBy(self)));
                            action.appendEffect(new DetachParasitesEffect(action, toDetach));
                        }
                    });

                }
            });
            actions.add(action);
        }

        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        if (GameConditions.canTarget(game, self, targetingReason, Filters.and(Filters.title("Mynock"), Filters.attachedTo(Filters.starfighter)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a Mynock to be lost");

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a Mynock to be lost", targetingReason, Filters.and(Filters.title("Mynock"), Filters.attachedTo(Filters.starfighter))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCreature) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalCreature = action.getPrimaryTargetCard(targetGroupId);

                            action.appendEffect(new LoseCardFromTableEffect(action, finalCreature));
                        }
                    });

                }
            });
            actions.add(action);
        }
        return actions;
    }
}