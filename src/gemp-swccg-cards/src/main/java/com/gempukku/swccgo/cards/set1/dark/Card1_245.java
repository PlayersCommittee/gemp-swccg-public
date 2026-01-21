package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToStarshipOrVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Evacuate?
 */
public class Card1_245 extends AbstractUsedInterrupt {
    public Card1_245() {
        super(Side.DARK, 6, "Evacuate?", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Escape pods are on many starships allowing those in peril to flee, an act considered cowardly by Imperial officers. 'We've analyzed their attack, sir, and there is a danger.'");
        setGameText("If your capital starship is about to be lost, unless Tarkin aboard, relocate your characters aboard to any one planet site or to one of your capital starships.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter ship = Filters.and(Filters.your(self), Filters.capital_starship, Filters.not(Filters.hasAboard(self,Filters.Tarkin)));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, ship)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, ship)) {

            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();

            final Filter destinationSiteFilter = Filters.planet_site;
            final Filter destinationShipFilter = Filters.and(Filters.your(self), Filters.capital_starship, Filters.not(cardToBeLost), Filters.canBeTargetedBy(self));
            final Filter destinationFilter = Filters.or(destinationSiteFilter, destinationShipFilter);

            if(GameConditions.canSpot(game, self, destinationFilter)) {
                final Filter aboardFilter = Filters.and(Filters.your(self), Filters.character, Filters.aboard(cardToBeLost), Filters.canBeTargetedBy(self));
                final int charactersAboardCount = Filters.countActive(game, self, aboardFilter);
                final int astromechsAboardCount = Filters.countActive(game, self, Filters.and(aboardFilter, Filters.astromech_droid));

                final Filter captivesAboardFilter = Filters.and(Filters.captive, Filters.aboard(cardToBeLost), Filters.canBeTargetedBy(self));
                final int captivesAboardCount = Filters.countAllOnTable(game,captivesAboardFilter);

                Collection<PhysicalCard> possibleSites = new HashSet<>();
                for (PhysicalCard card : Filters.filterActive(game, self, aboardFilter)) {
                    possibleSites.addAll(Filters.filterTopLocationsOnTable(game, Filters.and(destinationSiteFilter, Filters.locationCanBeRelocatedTo(card, false, false, false, 0, false, true))));
                }

                //potential ships must be able to hold everyone from the aboardFilter (and any captives)
                Collection<PhysicalCard> possibleShips = new HashSet<>();
                for (PhysicalCard potentialShip : Filters.filterActive(game, self, destinationShipFilter)) {
                    //a few key assumptions are made:
                    // - astromechs cannot be pilots
                    // - pilot limitations (ex: must be alien) apply to all pilot capacity
                    // - any ship with "pilots and passengers" slots (ex: Binder) does not also have dedicated pilot capacity
                    //      Eject! Eject! could break this (adding 1 dedicated pilot capacity) but it can't be played on capital ships

                    //record capacity on the potential ship
                    int availablePilotOnlyCapacity = game.getGameState().getAvailablePilotCapacity(game.getModifiersQuerying(), potentialShip, self);
                    if(potentialShip.getBlueprint().getPilotOrPassengerCapacity() > 0) availablePilotOnlyCapacity = 0; //avoid double counting (this capacity is in availablePassengerCapacity)
                    int availablePassengerCapacity = game.getGameState().getAvailablePassengerCapacity(game.getModifiersQuerying(), potentialShip, self);
                    int availableAstromechOnlyCapacity = game.getGameState().getAvailablePassengerCapacityForAstromech(game.getModifiersQuerying(), potentialShip, self) - availablePassengerCapacity;

                    int eligiblePilotsAboard = Filters.countActive(game, self, Filters.and(aboardFilter, potentialShip.getBlueprint().getValidPilotFilter(playerId, game, potentialShip, false)));

                    //with optimal usage of pilot and astromech capacity...
                    int maxPilotOnlyCapacityToFill = Math.min(availablePilotOnlyCapacity,eligiblePilotsAboard);
                    int maxAstromechCapacityToFill = Math.min(availableAstromechOnlyCapacity,astromechsAboardCount);
                    //...check passenger capacity needed to hold the rest
                    int mandatoryPassengersAboard = (charactersAboardCount + captivesAboardCount) - maxPilotOnlyCapacityToFill - maxAstromechCapacityToFill;
                    if(availablePassengerCapacity >= mandatoryPassengersAboard) {
                        for (PhysicalCard card : Filters.filterActive(game, self, aboardFilter)) {
                            if (Filters.vehicleOrShipCanBeRelocatedTo(card, false, false, false, 0, false, true).accepts(game, potentialShip)) {
                                possibleShips.add(potentialShip); //enough room and at least 1 character can relocate
                                break; //avoid adding more than once
                            }
                        }
                    }
                }

                Collection<PhysicalCard> possibleSitesAndShips = new HashSet<>();
                possibleSitesAndShips.addAll(possibleSites);
                possibleSitesAndShips.addAll(possibleShips);

                if (!possibleSitesAndShips.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate characters to a planet site or capital ship");

                    action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose planet site or ship to relocate characters", Filters.in(possibleSitesAndShips)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            PhysicalCard destination = action.getPrimaryTargetCard(targetGroupId);
                                            if (possibleSites.contains(destination)) {
                                                Collection<PhysicalCard> toRelocate = Filters.filterActive(game, self, Filters.and(aboardFilter, Filters.canBeRelocatedToLocation(destination, false, false, false, 0, false, true)));
                                                action.addAnimationGroup(toRelocate);
                                                action.appendEffect(new RelocateBetweenLocationsEffect(action, toRelocate, destination));
                                            }
                                            else if(possibleShips.contains(destination)) {
                                                // collect capacity stats again, here
                                                final int availablePilotCapacity = game.getGameState().getAvailablePilotCapacity(game.getModifiersQuerying(), destination, self);
                                                final int availablePassengerCapacity = game.getGameState().getAvailablePassengerCapacity(game.getModifiersQuerying(), destination, self);
                                                final int availableAstromechCapacity = game.getGameState().getAvailablePassengerCapacityForAstromech(game.getModifiersQuerying(), destination, self);

                                                final int maxAstromechCapacityToFill = Math.min(availableAstromechCapacity,astromechsAboardCount);

                                                int minAboardThatMustBePilots = charactersAboardCount - availablePassengerCapacity - maxAstromechCapacityToFill;
                                                if(minAboardThatMustBePilots < 0) minAboardThatMustBePilots = 0;

                                                Collection<PhysicalCard> validCharactersToRelocateAsPilots = new LinkedList<PhysicalCard>();
                                                Collection<PhysicalCard> validCharactersToRelocateAsPassengers = new LinkedList<PhysicalCard>();
                                                for (PhysicalCard characterToRelocate : Filters.filterActive(game, self, aboardFilter)) {
                                                    if (Filters.and(aboardFilter, destination.getBlueprint().getValidPilotFilter(playerId, game, destination, false)).accepts(game, characterToRelocate))
                                                        validCharactersToRelocateAsPilots.add(characterToRelocate);
                                                    if (Filters.and(aboardFilter, destination.getBlueprint().getValidPassengerFilter(playerId, game, destination, false)).accepts(game, characterToRelocate))
                                                        validCharactersToRelocateAsPassengers.add(characterToRelocate);
                                                }

                                                if((availablePilotCapacity > 0) && !validCharactersToRelocateAsPilots.isEmpty()) {
                                                    String choiceText = "Choose characters to relocate as pilots";
                                                    if(minAboardThatMustBePilots > 0) choiceText = choiceText + " (must choose at least " + minAboardThatMustBePilots + ")";
                                                    action.appendTargeting(
                                                            new TargetCardsOnTableEffect(action, playerId, choiceText, minAboardThatMustBePilots, availablePilotCapacity, Filters.in(validCharactersToRelocateAsPilots)) {
                                                                @Override
                                                                protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                                    action.addAnimationGroup(cardsToRelocate);
                                                                    action.addAnimationGroup(destination);
                                                                    for (PhysicalCard cardToRelocate : cardsToRelocate) {
                                                                        //relocate each selected pilot, one at a time
                                                                        if(Filters.canBeRelocated(false).accepts(game,cardToRelocate)) { //would be better to use a canBeRelocatedToVehicleOrShip, eventually?
                                                                            action.appendEffect(
                                                                                    new RelocateFromLocationToStarshipOrVehicle(action, cardToRelocate, destination, true, self));
                                                                        }
                                                                    }
                                                                    validCharactersToRelocateAsPassengers.removeAll(cardsToRelocate);

                                                                    if(!validCharactersToRelocateAsPassengers.isEmpty()) {
                                                                        action.addAnimationGroup(validCharactersToRelocateAsPassengers);
                                                                        for(PhysicalCard cardToRelocate : validCharactersToRelocateAsPassengers) {
                                                                            //relocate all other characters as passengers, one at a time
                                                                            if(Filters.canBeRelocated(false).accepts(game,cardToRelocate)) { //would be better to use a canBeRelocatedToVehicleOrShip, eventually?
                                                                                action.appendEffect(
                                                                                        new RelocateFromLocationToStarshipOrVehicle(action, cardToRelocate, destination, false, self));
                                                                            }
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                    );
                                                }
                                                else if(!validCharactersToRelocateAsPassengers.isEmpty()) {
                                                    action.addAnimationGroup(validCharactersToRelocateAsPassengers);
                                                    for(PhysicalCard cardToRelocate : validCharactersToRelocateAsPassengers) {
                                                        if(Filters.canBeRelocated(false).accepts(game,cardToRelocate)) {
                                                            action.appendEffect(
                                                                    new RelocateFromLocationToStarshipOrVehicle(action, cardToRelocate, destination, false, self));
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                //crash message? should be impossible
                                                game.getGameState().sendMessage("Card1_245 destination was not in sites or ships group. Please report this error.");
                                            }
                                        }
                                    }
                            );
                        }
                    });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}