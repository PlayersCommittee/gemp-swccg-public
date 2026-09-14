package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.List;

/**
 * Shared helper for rearranging related sites of a system.
 * The planet/system is a parameter so Death Star and Cloud City (Bespin) share
 * the same code. Docking Bay 327 is a separate LocationGroup after the interior
 * sites in DeathStarLayout, so rearranging the interior group leaves it at the
 * end automatically. Converted location stacks stay together because we permute
 * whole stacks, not individual cards. Cards at a site stay there because they
 * are attached to the location card, not to a table index.
 */
public final class RearrangeSites {

    private RearrangeSites() {
    }

    /**
     * Filter for interior-only sites of a system. Matches the "Interior sites"
     * LocationGroup used by DeathStarLayout and BespinLayout. Docking bays that
     * are also exterior (Docking Bay 327, Docking Bay 94) are excluded, as is
     * Death Star: Trench.
     * @param systemName the system title, for example Title.Death_Star or Title.Bespin
     * @return the interior-only site filter for that system
     */
    public static Filter interiorSitesOfSystem(String systemName) {
        // Matches Death Star / Bespin "Interior sites" groups and Naboo's interior
        // Theed Palace row (Throne Room is its own group; courtyard is also exterior).
        return Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site),
                Filters.not(Filters.underwater_site), Filters.not(Filters.Theed_Palace_Throne_Room),
                Filters.partOfSystem(systemName));
    }

    /**
     * True when that system's interior-site group is on the table with at least
     * one site. Zero matching sites means a card that rearranges them cannot start.
     * @param game the game
     * @param systemName the system title, for example Title.Death_Star
     * @return true if there is at least one interior site to rearrange
     */
    public static boolean canRearrangeInteriorSites(SwccgGame game, String systemName) {
        LocationGroup group = game.getGameState().getLocationsLayout()
                .findGroupForSystemMatching(game, systemName, interiorSitesOfSystem(systemName));
        return group != null && !group.getTopCardsInGroup().isEmpty();
    }

    /**
     * Reorders sites that already sit in the same LocationGroup so the given
     * top locations appear left-to-right in newTopOrder. Other sites in that
     * group keep their relative slots. Does not fire leave/move/deploy events.
     * @param game the game
     * @param newTopOrder the requested left-to-right order of top location cards
     * @return true if the order was applied or already matched; false if invalid
     */
    public static boolean rearrange(SwccgGame game, List<? extends PhysicalCard> newTopOrder) {
        return game.getGameState().reorderTopLocationsInGroup(newTopOrder);
    }

    /**
     * Reorders interior-only sites of the given system using the same helper
     * Death Star and Bespin share. newTopOrder must be a permutation of some
     * or all current interior sites of that system. An empty list does nothing.
     * @param game the game
     * @param systemName the system title, for example Title.Death_Star
     * @param newTopOrder the requested left-to-right order of those interior sites
     * @return true if the order was applied or already matched; false if invalid
     */
    public static boolean rearrangeInteriorSites(SwccgGame game, String systemName, List<? extends PhysicalCard> newTopOrder) {
        return game.getGameState().reorderTopLocationsInGroup(systemName, interiorSitesOfSystem(systemName), newTopOrder);
    }

    /**
     * Same as rearrangeInteriorSites, but the new order is a permutation of the
     * current stack indexes (0 is the current leftmost interior site).
     * @param game the game
     * @param systemName the system title, for example Title.Death_Star
     * @param permutation new left-to-right stack indexes
     * @return true if the order was applied or already matched; false if invalid
     */
    public static boolean rearrangeInteriorSitesByPermutation(SwccgGame game, String systemName, List<Integer> permutation) {
        return game.getGameState().reorderTopLocationsInGroupByPermutation(systemName, interiorSitesOfSystem(systemName), permutation);
    }
}
