package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.OpenSpaceSlugMouthEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: This Is No Cave
 */
public class Card4_063 extends AbstractUsedInterrupt {
    public Card4_063() {
        super(Side.LIGHT, 5, Title.This_Is_No_Cave, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("The largest space slug on record was 900 meters long. Han suspected he had discovered one that was somewhat larger.");
        setGameText("Relocate one starfighter from a Big One to the related site (or vice versa) for free (opens Slug's mouth if closed). If smuggler aboard, starfighter is immune to attrition and subtracts 5 from asteroid destiny until end of your next turn. OR Cancel Corrosive Damage.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Collection<PhysicalCard> validStarfighters = getValidRelocateTargets(game, self);
        if (!validStarfighters.isEmpty()) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate starfighter");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", Filters.in(validStarfighters)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            final PhysicalCard destination = getRelatedDestination(game, starfighter);
                            if (destination != null) {
                                action.addAnimationGroup(destination);
                            }
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(starfighter)
                                            + (destination != null ? (" to " + GameUtils.getCardLink(destination)) : ""),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalStarfighter = action.getPrimaryTargetCard(targetGroupId);
                                            final PhysicalCard finalDestination = getRelatedDestination(game, finalStarfighter);
                                            if (finalDestination == null) {
                                                return;
                                            }

                                            // Open Space Slug mouth if closed
                                            PhysicalCard spaceSlug = findRelatedSpaceSlug(game, finalStarfighter, finalDestination);
                                            if (spaceSlug != null && spaceSlug.isMouthClosed()) {
                                                action.appendEffect(
                                                        new OpenSpaceSlugMouthEffect(action, spaceSlug));
                                            }

                                            // Relocate
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, finalStarfighter, finalDestination));

                                            // Smuggler aboard: immune to attrition + -5 asteroid destiny until end of your next turn
                                            if (Filters.hasAboard(self, Filters.smuggler).accepts(game, finalStarfighter)) {
                                                action.appendEffect(
                                                        new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                                                new ImmuneToAttritionModifier(self, finalStarfighter),
                                                                "Makes " + GameUtils.getCardLink(finalStarfighter) + " immune to attrition"));
                                                action.appendEffect(
                                                        new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                                                new EachAsteroidDestinyModifier(self, -5, finalStarfighter),
                                                                "Subtracts 5 from asteroid destiny targeting " + GameUtils.getCardLink(finalStarfighter)));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Corrosive_Damage)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Corrosive_Damage, Title.Corrosive_Damage);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Corrosive_Damage)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    private Collection<PhysicalCard> getValidRelocateTargets(SwccgGame game, PhysicalCard self) {
        List<PhysicalCard> valid = new LinkedList<PhysicalCard>();
        Filter starfighterFilter = Filters.and(Filters.starfighter, Filters.canBeTargetedBy(self),
                Filters.or(Filters.at(Filters.Big_One), Filters.at(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly)));
        for (PhysicalCard starfighter : Filters.filterActive(game, self, starfighterFilter)) {
            PhysicalCard destination = getRelatedDestination(game, starfighter);
            if (destination != null && canRelocateIgnoringClosedMouth(game, starfighter, destination)) {
                valid.add(starfighter);
            }
        }
        return valid;
    }

    private PhysicalCard getRelatedDestination(SwccgGame game, PhysicalCard starfighter) {
        PhysicalCard location = starfighter.getAtLocation();
        if (location == null) {
            return null;
        }
        if (Filters.Big_One.accepts(game, location)) {
            return Filters.findFirstFromTopLocationsOnTable(game,
                    Filters.and(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, Filters.relatedSite(location)));
        }
        if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(game, location)) {
            return Filters.findFirstFromTopLocationsOnTable(game,
                    Filters.and(Filters.Big_One, Filters.relatedLocation(location)));
        }
        return null;
    }

    private boolean canRelocateIgnoringClosedMouth(SwccgGame game, PhysicalCard starfighter, PhysicalCard destination) {
        if (Filters.canBeRelocatedToLocation(destination, true, false, true, 0, false).accepts(game, starfighter)) {
            return true;
        }
        PhysicalCard spaceSlug = findRelatedSpaceSlug(game, starfighter, destination);
        if (spaceSlug == null || !spaceSlug.isMouthClosed()) {
            return false;
        }
        // Mouth is closed; card will open it. Re-check relocate eligibility with mouth temporarily open.
        spaceSlug.setMouthClosed(false);
        boolean ok = Filters.canBeRelocatedToLocation(destination, true, false, true, 0, false).accepts(game, starfighter);
        spaceSlug.setMouthClosed(true);
        return ok;
    }

    private PhysicalCard findRelatedSpaceSlug(SwccgGame game, PhysicalCard starfighter, PhysicalCard destination) {
        PhysicalCard fromLocation = starfighter.getAtLocation();
        PhysicalCard bigOne = null;
        if (fromLocation != null && Filters.Big_One.accepts(game, fromLocation)) {
            bigOne = fromLocation;
        }
        else if (destination != null && Filters.Big_One.accepts(game, destination)) {
            bigOne = destination;
        }
        else if (fromLocation != null) {
            bigOne = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Big_One, Filters.relatedLocation(fromLocation)));
        }
        if (bigOne == null) {
            return null;
        }
        return Filters.findFirstFromAllOnTable(game, Filters.and(Filters.Space_Slug, Filters.at(bigOne)));
    }
}
