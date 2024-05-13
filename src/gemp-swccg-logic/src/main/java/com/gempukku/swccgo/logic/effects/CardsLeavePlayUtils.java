package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;
import java.util.List;

public class CardsLeavePlayUtils {

    /**
     * Figures out the cards also leave play (or released) when the specified cards are leaving the table.
     * @param game the game
     * @param cardsLeavingPlay the cards leaving the table
     * @param releaseCaptives true if captives are released, otherwise false
     * @param additionalToLeavePlay additional cards to leave play (since attached/aboard cards leaving play) are added to this list
     * @param releasedCaptives cards that are released are added to this list (since escort is leaving play, unless not releasing captives)
     */
    public static void cardsToLeavePlay(SwccgGame game, Collection<PhysicalCard> cardsLeavingPlay, boolean releaseCaptives,
                                        Collection<PhysicalCard> additionalToLeavePlay, Collection<PhysicalCard> releasedCaptives) {
        for (PhysicalCard card : cardsLeavingPlay) {
            cardsToLeavePlay(game, cardsLeavingPlay, card, releaseCaptives, additionalToLeavePlay, releasedCaptives);
        }
    }

    private static void cardsToLeavePlay(SwccgGame game, Collection<PhysicalCard> cardsLeavingPlay, PhysicalCard card, boolean releaseCaptives,
                                         Collection<PhysicalCard> additionalToLeavePlay, Collection<PhysicalCard> releasedCaptives) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        List<PhysicalCard> attachedCards = gameState.getAttachedCards(card, true);
        for (PhysicalCard attachedCard : attachedCards) {

            // If card not already checked.
            if (!cardsLeavingPlay.contains(attachedCard)
                    && !additionalToLeavePlay.contains(attachedCard)
                    && !releasedCaptives.contains(attachedCard)) {
                // If releasing captives, then captives are added to release list.
                if (releaseCaptives && attachedCard.isCaptive()) {
                    releasedCaptives.add(attachedCard);
                }
                else {
                    additionalToLeavePlay.add(attachedCard);
                    cardsToLeavePlay(game, cardsLeavingPlay, attachedCard, false, additionalToLeavePlay, releasedCaptives);
                }
            }
        }

        // If card is a starship or vehicle also, check for any related non-unique starship/vehicle sites
        if (card.getBlueprint().getCardCategory() == CardCategory.STARSHIP || card.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {
            Collection<PhysicalCard> nonuniqueStarshipVehicleSites = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.non_unique, Filters.or(Filters.starship_site, Filters.vehicle_site), Filters.siteOfStarshipOrVehicle(card)));

            for (PhysicalCard nonuniqueStarshipVehicleSite : nonuniqueStarshipVehicleSites) {

                // If card not already checked.
                if (!cardsLeavingPlay.contains(nonuniqueStarshipVehicleSite)
                        && !additionalToLeavePlay.contains(nonuniqueStarshipVehicleSite)
                        && !releasedCaptives.contains(nonuniqueStarshipVehicleSite)) {
                    additionalToLeavePlay.add(nonuniqueStarshipVehicleSite);
                    cardsToLeavePlay(game, cardsLeavingPlay, nonuniqueStarshipVehicleSite, false, additionalToLeavePlay, releasedCaptives);
                }
            }
        }

        // If card is a top location, check for any cards directly at the location and any converted locations under it.
        if (card.getZone() == Zone.LOCATIONS) {
            List<PhysicalCard> cardsAtLocation = gameState.getCardsAtLocation(card);
            for (PhysicalCard cardAtLocation : cardsAtLocation) {

                // If card not already checked.
                if (!cardsLeavingPlay.contains(cardAtLocation)
                        && !additionalToLeavePlay.contains(cardAtLocation)
                        && !releasedCaptives.contains(cardAtLocation)) {
                    additionalToLeavePlay.add(cardAtLocation);
                    cardsToLeavePlay(game, cardsLeavingPlay, cardAtLocation, false, additionalToLeavePlay, releasedCaptives);
                }
            }

            List<PhysicalCard> convertedLocations = gameState.getConvertedLocationsUnderTopLocation(card);
            for (PhysicalCard convertedLocation : convertedLocations) {

                // If card not already checked.
                if (!cardsLeavingPlay.contains(convertedLocation)
                        && !additionalToLeavePlay.contains(convertedLocation)
                        && !releasedCaptives.contains(convertedLocation)) {
                    additionalToLeavePlay.add(convertedLocation);
                }
            }
        }

        // If card is a Space Slug, check for cards at a related Space Slug Belly
        if (Filters.Space_Slug.accepts(game, card)) {
            PhysicalCard spaceSlugBelly = Filters.findFirstFromTopLocationsOnTable(game,
                    Filters.and(Filters.Space_Slug_Belly, Filters.relatedSite(card)));
            if (spaceSlugBelly != null) {

                Collection<PhysicalCard> cardsAtLocation = Filters.filterAllOnTable(game, Filters.at(spaceSlugBelly));
                for (PhysicalCard cardAtLocation : cardsAtLocation) {

                    // If card not already checked.
                    if (!cardsLeavingPlay.contains(cardAtLocation)
                            && !additionalToLeavePlay.contains(cardAtLocation)
                            && !releasedCaptives.contains(cardAtLocation)) {
                        additionalToLeavePlay.add(cardAtLocation);
                        cardsToLeavePlay(game, cardsLeavingPlay, cardAtLocation, false, additionalToLeavePlay, releasedCaptives);
                    }
                }
            }
        }

        // Check stacked cards
        List<PhysicalCard> stackedCards = gameState.getStackedCards(card);
        for (PhysicalCard stackedCard : stackedCards) {

            // If card not already checked.
            if (!cardsLeavingPlay.contains(stackedCard)
                    && !additionalToLeavePlay.contains(stackedCard)
                    && !releasedCaptives.contains(stackedCard)) {
                additionalToLeavePlay.add(stackedCard);
                cardsToLeavePlay(game, cardsLeavingPlay, stackedCard, false, additionalToLeavePlay, releasedCaptives);
            }
        }
    }
}
