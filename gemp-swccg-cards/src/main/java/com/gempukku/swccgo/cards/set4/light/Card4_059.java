package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelAttackEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Shoo! Shoo!
 */
public class Card4_059 extends AbstractUsedInterrupt {
    public Card4_059() {
        super(Side.LIGHT, 6, "Shoo! Shoo!", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("'Oh! Go away! Go away! Beastly thing!'");
        setGameText("If you have a droid on table: Cancel an attack just initiated by a creature. OR Immediately move one opponent's creature (except an attached creature) to an adjacent location (habitat permitting).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.droid))
                && TriggerConditions.attackInitiatedByCreature(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel attack");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelAttackEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter filter = Filters.and(Filters.opponents(playerId), Filters.creature, Filters.not(Filters.attachedTo(Filters.any)),
                Filters.at(Filters.or(Filters.adjacentSiteTo(self, Filters.site), Filters.adjacentSectorTo(self, Filters.sector))),
                Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.droid))
                && GameConditions.canTarget(game, self, filter)) {

            Collection<PhysicalCard> possibleTargets = new LinkedList<>();
            for (PhysicalCard creature: Filters.filterAllOnTable(game, filter)) {
                Filter habitat = creature.getBlueprint().getHabitatFilter(game, creature);

                if (Filters.canSpotFromTopLocationsOnTable(game, Filters.and(habitat, Filters.or(Filters.adjacentSiteTo(self, Filters.atSameLocation(creature)), Filters.adjacentSectorTo(self, Filters.atSameLocation(creature)))))) {
                    possibleTargets.add(creature);
                }
            }

            if (!possibleTargets.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move an opponent's creature");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target an opponent's creature to move", Filters.in(possibleTargets)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedCreature) {
                        Filter habitat = targetedCreature.getBlueprint().getHabitatFilter(game, targetedCreature);
                        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target location to move creature", Filters.and(habitat, Filters.or(Filters.adjacentSiteTo(self, Filters.atSameLocation(targetedCreature)), Filters.adjacentSectorTo(self, Filters.atSameLocation(targetedCreature))))) {
                            @Override
                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedLocation) {
                                action.allowResponses(new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        PhysicalCard finalCreature = action.getPrimaryTargetCard(targetGroupId1);
                                        PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId2);

                                        action.appendEffect(new RelocateBetweenLocationsEffect(action, finalCreature, finalLocation));
                                    }
                                });
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