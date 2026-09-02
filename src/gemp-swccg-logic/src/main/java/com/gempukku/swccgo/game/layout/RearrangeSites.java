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
        return Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName));
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
     * or all current interior sites of that system.
     * @param game the game
     * @param systemName the system title, for example Title.Death_Star
     * @param newTopOrder the requested left-to-right order of those interior sites
     * @return true if the order was applied or already matched; false if invalid
     */
    public static boolean rearrangeInteriorSites(SwccgGame game, String systemName, List<? extends PhysicalCard> newTopOrder) {
        return game.getGameState().reorderTopLocationsInGroup(systemName, interiorSitesOfSystem(systemName), newTopOrder);
    }
}