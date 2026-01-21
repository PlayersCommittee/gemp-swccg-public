package com.gempukku.swccgo.ai.common;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for analyzing board state.
 *
 * Provides convenient methods for AI to evaluate:
 * - Power at locations
 * - Location control/contestation status
 * - Force icon counts
 * - Presence analysis
 *
 * Uses GEMP's ModifiersQuerying for accurate game-aware values.
 */
public class AiBoardAnalyzer {

    /**
     * Contestation status at a location.
     */
    public enum ContestStatus {
        WINNING,      // We have more power/presence than opponent
        LOSING,       // Opponent has more power/presence
        TIED,         // Equal power/presence
        UNCONTESTED,  // Only one player has presence (or neither)
        EMPTY         // Nobody has presence
    }

    /**
     * Analysis result for a location.
     * Ported from Python deploy_planner.py LocationAnalysis dataclass.
     */
    public static class LocationAnalysis {
        public final PhysicalCard location;
        public final float ourPower;
        public final float theirPower;
        public final float ourAbility;
        public final float theirAbility;
        public final int ourForceIcons;
        public final int theirForceIcons;
        public final int ourCardCount;
        public final int theirCardCount;
        public final ContestStatus status;
        public final boolean isBattleground;

        // Location type flags (like Python)
        public final boolean isInterior;
        public final boolean isExterior;
        public final boolean isSite;
        public final boolean isSystem;

        // Strategic flags (like Python)
        public boolean shouldFlee = false;       // We're badly outpowered, should retreat
        public boolean isBattleOpportunity = false;  // Good opportunity to initiate battle

        // Location index for move planning
        public int locationIndex = -1;

        public LocationAnalysis(PhysicalCard location, float ourPower, float theirPower,
                               float ourAbility, float theirAbility, int ourForceIcons,
                               int theirForceIcons, int ourCardCount, int theirCardCount,
                               ContestStatus status, boolean isBattleground,
                               boolean isInterior, boolean isExterior, boolean isSite, boolean isSystem) {
            this.location = location;
            this.ourPower = ourPower;
            this.theirPower = theirPower;
            this.ourAbility = ourAbility;
            this.theirAbility = theirAbility;
            this.ourForceIcons = ourForceIcons;
            this.theirForceIcons = theirForceIcons;
            this.ourCardCount = ourCardCount;
            this.theirCardCount = theirCardCount;
            this.status = status;
            this.isBattleground = isBattleground;
            this.isInterior = isInterior;
            this.isExterior = isExterior;
            this.isSite = isSite;
            this.isSystem = isSystem;
        }

        // Backwards-compatible constructor
        public LocationAnalysis(PhysicalCard location, float ourPower, float theirPower,
                               float ourAbility, float theirAbility, int ourForceIcons,
                               int theirForceIcons, int ourCardCount, int theirCardCount,
                               ContestStatus status, boolean isBattleground) {
            this(location, ourPower, theirPower, ourAbility, theirAbility, ourForceIcons,
                theirForceIcons, ourCardCount, theirCardCount, status, isBattleground,
                false, true, true, false);  // Default: exterior site
        }

        /** Get power advantage (positive = we're winning) */
        public float getPowerAdvantage() {
            return ourPower - theirPower;
        }

        /** Check if location is contested (both players have presence) */
        public boolean isContested() {
            return ourCardCount > 0 && theirCardCount > 0;
        }

        /** Check if we control this location (more ability, no opponent presence) */
        public boolean weControl() {
            return ourAbility > 0 && theirAbility == 0;
        }

        /** Check if opponent controls this location */
        public boolean theyControl() {
            return theirAbility > 0 && ourAbility == 0;
        }

        /** Check if this is a ground location (site or exterior) */
        public boolean isGround() {
            if (location == null || location.getBlueprint() == null) {
                return false;
            }
            CardCategory category = location.getBlueprint().getCardCategory();
            if (category != CardCategory.LOCATION) {
                return false;
            }
            // If it's not a system, it's ground (site)
            return location.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SYSTEM;
        }

        /** Check if this is a space location (system) */
        public boolean isSpace() {
            if (location == null || location.getBlueprint() == null) {
                return false;
            }
            CardCategory category = location.getBlueprint().getCardCategory();
            if (category != CardCategory.LOCATION) {
                return false;
            }
            return location.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM;
        }
    }

    // =========================================================================
    // Power Analysis
    // =========================================================================

    /**
     * Get total power a player has at a location.
     *
     * @param game the current game
     * @param playerId the player
     * @param location the location
     * @return total power at location
     */
    public static float getPowerAtLocation(SwccgGame game, String playerId, PhysicalCard location) {
        if (game == null || playerId == null || location == null) {
            return 0;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) {
            return 0;
        }

        float totalPower = 0;
        Collection<PhysicalCard> cardsAtLocation = Filters.filterActive(game, null,
            Filters.and(Filters.owner(playerId), Filters.atLocation(location)));

        for (PhysicalCard card : cardsAtLocation) {
            CardCategory category = card.getBlueprint().getCardCategory();
            if (category == CardCategory.CHARACTER || category == CardCategory.STARSHIP
                || category == CardCategory.VEHICLE) {
                totalPower += modifiersQuerying.getPower(gameState, card);
            }
        }

        return totalPower;
    }

    /**
     * Get total ability a player has at a location.
     *
     * @param game the current game
     * @param playerId the player
     * @param location the location
     * @return total ability at location
     */
    public static float getAbilityAtLocation(SwccgGame game, String playerId, PhysicalCard location) {
        if (game == null || playerId == null || location == null) {
            return 0;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) {
            return 0;
        }

        return modifiersQuerying.getTotalAbilityAtLocation(gameState, playerId, location);
    }

    /**
     * Count cards a player has at a location.
     *
     * @param game the current game
     * @param playerId the player
     * @param location the location
     * @return number of cards at location
     */
    public static int getCardCountAtLocation(SwccgGame game, String playerId, PhysicalCard location) {
        if (game == null || playerId == null || location == null) {
            return 0;
        }

        Collection<PhysicalCard> cards = Filters.filterActive(game, null,
            Filters.and(Filters.owner(playerId), Filters.atLocation(location)));
        return cards.size();
    }

    // =========================================================================
    // Force Icon Analysis
    // =========================================================================

    /**
     * Get total force icons a player has across all locations.
     *
     * @param game the current game
     * @param side the side (DARK or LIGHT)
     * @return total force icons
     */
    public static int getTotalForceIcons(SwccgGame game, Side side) {
        if (game == null || side == null) {
            return 0;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) {
            return 0;
        }

        Icon forceIcon = (side == Side.DARK) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
        int totalIcons = 0;

        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.any);
        for (PhysicalCard location : locations) {
            totalIcons += modifiersQuerying.getIconCount(gameState, location, forceIcon);
        }

        return totalIcons;
    }

    /**
     * Get force icons at a specific location for a side.
     *
     * @param game the current game
     * @param location the location
     * @param side the side (DARK or LIGHT)
     * @return force icons at location
     */
    public static int getForceIconsAtLocation(SwccgGame game, PhysicalCard location, Side side) {
        if (game == null || location == null || side == null) {
            return 0;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) {
            return 0;
        }

        Icon forceIcon = (side == Side.DARK) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
        return modifiersQuerying.getIconCount(gameState, location, forceIcon);
    }

    // =========================================================================
    // Location Analysis
    // =========================================================================

    /**
     * Analyze a location to get power, ability, and contestation status.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param location the location to analyze
     * @param ourSide our side
     * @return LocationAnalysis with detailed information
     */
    public static LocationAnalysis analyzeLocation(SwccgGame game, String playerId,
            String opponentId, PhysicalCard location, Side ourSide) {
        if (game == null || playerId == null || opponentId == null || location == null) {
            return new LocationAnalysis(location, 0, 0, 0, 0, 0, 0, 0, 0, ContestStatus.EMPTY, false);
        }

        float ourPower = getPowerAtLocation(game, playerId, location);
        float theirPower = getPowerAtLocation(game, opponentId, location);
        float ourAbility = getAbilityAtLocation(game, playerId, location);
        float theirAbility = getAbilityAtLocation(game, opponentId, location);
        int ourCount = getCardCountAtLocation(game, playerId, location);
        int theirCount = getCardCountAtLocation(game, opponentId, location);

        Side theirSide = (ourSide == Side.DARK) ? Side.LIGHT : Side.DARK;
        int ourForceIcons = getForceIconsAtLocation(game, location, ourSide);
        int theirForceIcons = getForceIconsAtLocation(game, location, theirSide);

        // Determine contest status
        ContestStatus status;
        if (ourCount == 0 && theirCount == 0) {
            status = ContestStatus.EMPTY;
        } else if (ourCount == 0 || theirCount == 0) {
            status = ContestStatus.UNCONTESTED;
        } else if (ourPower > theirPower) {
            status = ContestStatus.WINNING;
        } else if (theirPower > ourPower) {
            status = ContestStatus.LOSING;
        } else {
            status = ContestStatus.TIED;
        }

        // Check if battleground
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        boolean isBattleground = false;
        if (gameState != null && modifiersQuerying != null) {
            isBattleground = modifiersQuerying.isBattleground(gameState, location, null);
        }

        // Detect location type (interior/exterior, site/system)
        boolean isInterior = false;
        boolean isExterior = false;
        boolean isSite = false;
        boolean isSystem = false;

        if (location.getBlueprint() != null) {
            // Check icons for interior/exterior
            isInterior = location.getBlueprint().hasIcon(Icon.INTERIOR_SITE);
            isExterior = location.getBlueprint().hasIcon(Icon.EXTERIOR_SITE);

            // Check subtype for site/system
            com.gempukku.swccgo.common.CardSubtype subtype = location.getBlueprint().getCardSubtype();
            isSystem = (subtype == com.gempukku.swccgo.common.CardSubtype.SYSTEM);
            isSite = (subtype == com.gempukku.swccgo.common.CardSubtype.SITE);

            // If neither interior nor exterior, default to exterior for sites
            if (isSite && !isInterior && !isExterior) {
                isExterior = true;
            }
        }

        LocationAnalysis analysis = new LocationAnalysis(location, ourPower, theirPower, ourAbility, theirAbility,
            ourForceIcons, theirForceIcons, ourCount, theirCount, status, isBattleground,
            isInterior, isExterior, isSite, isSystem);

        // Set strategic flags
        // shouldFlee: We're badly outpowered (Python uses BATTLE_DANGER_THRESHOLD = -6)
        float powerDiff = ourPower - theirPower;
        if (ourCount > 0 && theirCount > 0 && powerDiff <= -6) {
            analysis.shouldFlee = true;
        }

        // isBattleOpportunity: We have good advantage (Python uses BATTLE_FAVORABLE_THRESHOLD = 4)
        if (ourCount > 0 && theirCount > 0 && powerDiff >= 4) {
            analysis.isBattleOpportunity = true;
        }

        return analysis;
    }

    /**
     * Get all locations on the table.
     *
     * @param game the current game
     * @return collection of location cards
     */
    public static Collection<PhysicalCard> getAllLocations(SwccgGame game) {
        if (game == null) {
            return new ArrayList<>();
        }
        return Filters.filterTopLocationsOnTable(game, Filters.any);
    }

    /**
     * Analyze all locations and return list of analyses.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return list of LocationAnalysis for all locations
     */
    public static List<LocationAnalysis> analyzeAllLocations(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        List<LocationAnalysis> analyses = new ArrayList<>();
        Collection<PhysicalCard> locations = getAllLocations(game);

        for (PhysicalCard location : locations) {
            analyses.add(analyzeLocation(game, playerId, opponentId, location, ourSide));
        }

        return analyses;
    }

    // =========================================================================
    // Strategic Queries
    // =========================================================================

    /**
     * Find contested locations where we're losing.
     *
     * These are priority locations for reinforcement.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return list of locations where we're losing
     */
    public static List<LocationAnalysis> getLosingLocations(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        List<LocationAnalysis> losing = new ArrayList<>();

        for (LocationAnalysis analysis : analyzeAllLocations(game, playerId, opponentId, ourSide)) {
            if (analysis.status == ContestStatus.LOSING) {
                losing.add(analysis);
            }
        }

        return losing;
    }

    /**
     * Find locations where we're winning (have more power).
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return list of locations where we're winning
     */
    public static List<LocationAnalysis> getWinningLocations(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        List<LocationAnalysis> winning = new ArrayList<>();

        for (LocationAnalysis analysis : analyzeAllLocations(game, playerId, opponentId, ourSide)) {
            if (analysis.status == ContestStatus.WINNING) {
                winning.add(analysis);
            }
        }

        return winning;
    }

    /**
     * Find locations with opponent presence but no our presence.
     *
     * These are good targets to challenge.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return list of opponent-only locations
     */
    public static List<LocationAnalysis> getOpponentOnlyLocations(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        List<LocationAnalysis> opponentOnly = new ArrayList<>();

        for (LocationAnalysis analysis : analyzeAllLocations(game, playerId, opponentId, ourSide)) {
            if (analysis.theirCardCount > 0 && analysis.ourCardCount == 0) {
                opponentOnly.add(analysis);
            }
        }

        return opponentOnly;
    }

    /**
     * Find battleground locations we control (have presence, opponent doesn't).
     *
     * These are locations we can Force drain from.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return list of controlled battleground locations
     */
    public static List<LocationAnalysis> getControlledBattlegrounds(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        List<LocationAnalysis> controlled = new ArrayList<>();

        for (LocationAnalysis analysis : analyzeAllLocations(game, playerId, opponentId, ourSide)) {
            if (analysis.isBattleground && analysis.weControl()) {
                controlled.add(analysis);
            }
        }

        return controlled;
    }

    /**
     * Calculate overall board advantage.
     *
     * Positive = we're ahead, negative = opponent is ahead.
     * Considers: power at contested locations, controlled locations, force icons.
     *
     * @param game the current game
     * @param playerId our player ID
     * @param opponentId opponent's player ID
     * @param ourSide our side
     * @return board advantage score
     */
    public static float calculateBoardAdvantage(SwccgGame game, String playerId,
            String opponentId, Side ourSide) {
        float advantage = 0;

        List<LocationAnalysis> analyses = analyzeAllLocations(game, playerId, opponentId, ourSide);

        for (LocationAnalysis analysis : analyses) {
            // Power advantage at contested locations matters most
            if (analysis.isContested()) {
                advantage += analysis.getPowerAdvantage() * 0.5f;
            }

            // Uncontested presence is also valuable
            if (analysis.weControl()) {
                advantage += 2.0f;
            } else if (analysis.theyControl()) {
                advantage -= 2.0f;
            }

            // Force icons provide ongoing advantage
            advantage += (analysis.ourForceIcons - analysis.theirForceIcons) * 0.25f;
        }

        return advantage;
    }
}
