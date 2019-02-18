package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Closer?!
 */
public class Card4_047 extends AbstractUsedInterrupt {
    public Card4_047() {
        super(Side.LIGHT, 6, Title.Closer, Uniqueness.UNIQUE);
        setLore("'I'm going in closer to one of those big ones.' 'Closer?!' 'Closer?!' 'Graarg?!'");
        setGameText("During your move phase, use 1 Force to relocate one of your starships from a planet system to any related asteroid sector (or vice versa). This movement is free if a smuggler is on board. OR Cancel Rogue Asteroid or Close Call.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canSpotLocation(game, Filters.asteroid_sector)) {
            List<PhysicalCard> validStarships = new LinkedList<PhysicalCard>();
            // Figure out which starships can be relocated to a related asteroid sector
            Collection<PhysicalCard> starshipsAtPlanetSystem = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.starship, Filters.at(Filters.planet_system), Filters.canBeTargetedBy(self)));
            for (PhysicalCard starship : starshipsAtPlanetSystem) {
                if (Filters.hasAboard(self, Filters.smuggler).accepts(game, starship)) {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.relatedAsteroidSector(starship)), true, 0).accepts(game, starship)) {
                        validStarships.add(starship);
                    }
                }
                else {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.relatedAsteroidSector(starship)), 1).accepts(game, starship)) {
                        validStarships.add(starship);
                    }
                }
            }
            // Figure out which starships can be relocated to related planet system
            Collection<PhysicalCard> starshipsAtAsteroidSector = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.starship, Filters.at(Filters.asteroid_sector), Filters.canBeTargetedBy(self)));
            for (PhysicalCard starship : starshipsAtAsteroidSector) {
                if (Filters.hasAboard(self, Filters.smuggler).accepts(game, starship)) {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.relatedSystem(starship)), true, 0).accepts(game, starship)) {
                        validStarships.add(starship);
                    }
                }
                else {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.relatedSystem(starship)), 1).accepts(game, starship)) {
                        validStarships.add(starship);
                    }
                }
            }
            if (!validStarships.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate starship");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose starship", Filters.in(validStarships)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard starshipToRelocate) {
                                action.addAnimationGroup(starshipToRelocate);
                                final boolean smugglerAboard = Filters.hasAboard(self, Filters.smuggler).accepts(game, starshipToRelocate);
                                Filter locationFilter;
                                if (Filters.at(Filters.planet_system).accepts(game, starshipToRelocate)) {
                                    locationFilter = Filters.and(Filters.relatedAsteroidSector(starshipToRelocate), Filters.locationCanBeRelocatedTo(starshipToRelocate, false, false, smugglerAboard, smugglerAboard ? 0 : 1, false));                                }
                                else {
                                    locationFilter = Filters.and(Filters.relatedSystem(starshipToRelocate), Filters.locationCanBeRelocatedTo(starshipToRelocate, false, false, smugglerAboard, smugglerAboard ? 0 : 1, false));
                                }
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose location to relocate " + GameUtils.getCardLink(starshipToRelocate) + " to", locationFilter) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard locationSelected) {
                                                action.addAnimationGroup(locationSelected);
                                                // Pay cost(s)
                                                if (!smugglerAboard) {
                                                    action.appendCost(
                                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, starshipToRelocate, locationSelected, 1));
                                                }
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(starshipToRelocate) + " to " + GameUtils.getCardLink(locationSelected),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalStarship = action.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, finalStarship, locationSelected));
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
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rogue_Asteroid)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rogue_Asteroid, Title.Rogue_Asteroid);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Close_Call)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Close_Call, Title.Close_Call);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Rogue_Asteroid)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Close_Call)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}