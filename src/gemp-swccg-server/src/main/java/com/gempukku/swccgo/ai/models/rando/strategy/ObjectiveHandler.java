package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Objective Handler
 *
 * Handles SWCCG objective card requirements for starting card selection.
 * Each objective has specific cards that must be deployed at game start.
 *
 * When the bot receives an ARBITRARY_CARDS decision during "Play starting cards"
 * phase, this handler identifies which cards are required by the objective.
 *
 * Ported from Python objective_handler.py
 */
public class ObjectiveHandler {
    private static final Logger LOG = RandoLogger.getStrategyLogger();

    // =============================================================================
    // OBJECTIVE REQUIREMENTS DATABASE
    // Each entry: objective_blueprint_id -> list of required card patterns
    //
    // Patterns can be:
    // - Exact blueprint_id (e.g., "221_54" for Clone Command Center)
    // - "title:" prefix for title match (e.g., "title:Cloning Cylinders")
    // - "characteristic:" prefix (e.g., "characteristic:clone army battleground")
    // =============================================================================

    private static final Map<String, List<String>> OBJECTIVE_REQUIREMENTS = new LinkedHashMap<>();

    static {
        initializeObjectiveRequirements();
    }

    private static void initializeObjectiveRequirements() {
        // ==========================================================================
        // LIGHT SIDE OBJECTIVES
        // ==========================================================================

        // Hunt For The Droid General / He's A Coward (221_67)
        OBJECTIVE_REQUIREMENTS.put("221_67", Arrays.asList(
            "211_42",  // Kamino: Clone Birthing Center
            "221_54",  // Clone Command Center
            "211_53",  // Cloning Cylinders
            "221_65"   // Grievous Will Run And Hide
        ));

        // Massassi Base Operations / One In A Million (111_4)
        OBJECTIVE_REQUIREMENTS.put("111_4", Arrays.asList(
            "1_296",   // Yavin 4 (system)
            "1_297"    // Yavin 4: Docking Bay
        ));

        // Yavin 4 Base Operations / The Time To Fight Is Now (208_26)
        OBJECTIVE_REQUIREMENTS.put("208_26", Arrays.asList(
            "1_296",   // Yavin 4 (system)
            "1_139"    // Massassi War Room
        ));

        // City In The Clouds / You Truly Belong Here With Us (301_2)
        OBJECTIVE_REQUIREMENTS.put("301_2", Arrays.asList(
            "5_164",         // Bespin (system)
            "title:Cloud City"  // Any Cloud City battleground
        ));

        // Quiet Mining Colony / Independent Operation (109_4)
        OBJECTIVE_REQUIREMENTS.put("109_4", Arrays.asList(
            "5_164",         // Bespin (system)
            "title:Cloud City"  // Any Cloud City battleground
        ));

        // Mind What You Have Learned / Save You It Can (V) (225_53)
        OBJECTIVE_REQUIREMENTS.put("225_53", Arrays.asList(
            "225_40",       // Beldon's Corridor
            "4_89",         // Yoda's Hut
            "4_28",         // No Disintegrations!
            "title:Patience"  // Patience
        ));

        // We Have A Plan / They Will Be Lost And Confused (14_52)
        OBJECTIVE_REQUIREMENTS.put("14_52", Arrays.asList(
            "12_174",  // Theed Palace Throne Room
            "14_112",  // Theed Palace Hallway
            "12_172"   // Theed Palace Courtyard
        ));

        // Agents In The Court / No Love For The Empire (112_1)
        OBJECTIVE_REQUIREMENTS.put("112_1", Arrays.asList(
            "112_9",              // Hutt Trade Route
            "title:Jabba's Palace"  // Any Jabba's Palace site
        ));

        // My Kind Of Scum / Fearless And Inventive (112_15)
        OBJECTIVE_REQUIREMENTS.put("112_15", Arrays.asList(
            "112_20",             // Desert Heart
            "title:Jabba's Palace"  // Any Jabba's Palace site
        ));

        // He Is The Chosen One / He Will Bring Balance (208_25)
        OBJECTIVE_REQUIREMENTS.put("208_25", Arrays.asList(
            "217_34",  // Anakin's Funeral Pyre
            "8_163",   // Ewok Village
            "9_34"     // I Feel The Conflict
        ));

        // They Have No Idea We're Coming / Until We Win (209_29)
        OBJECTIVE_REQUIREMENTS.put("209_29", Arrays.asList(
            "216_13",  // Scarif (system)
            "209_25",  // Data Vault
            "1_139"    // Massassi War Room
        ));

        // The Galaxy May Need A Legend / We Need Luke Skywalker (211_36)
        OBJECTIVE_REQUIREMENTS.put("211_36", Collections.singletonList(
            "211_48"   // Ahch-To (system)
        ));

        // Old Allies / We Need Your Help (204_32)
        OBJECTIVE_REQUIREMENTS.put("204_32", Arrays.asList(
            "204_51",  // Jakku (system)
            "204_27"   // Niima Outpost Shipyard
        ));

        // Zero Hour / Liberation Of Lothal (219_48)
        OBJECTIVE_REQUIREMENTS.put("219_48", Arrays.asList(
            "219_10",      // Lothal (system)
            "title:Lothal:"  // Any Lothal site
        ));

        // Twin Suns Of Tatooine / Well Trained In The Jedi Arts (301_4)
        OBJECTIVE_REQUIREMENTS.put("301_4", Arrays.asList(
            "1_127",         // Tatooine (Light system)
            "title:Tatooine:"  // Any Tatooine site (non-Jabba's Palace)
        ));

        // ==========================================================================
        // DARK SIDE OBJECTIVES
        // ==========================================================================

        // Agents Of Black Sun / Vengeance Of The Dark Prince (10_29)
        OBJECTIVE_REQUIREMENTS.put("10_29", Arrays.asList(
            "7_277",    // Imperial City
            "200_144"   // Coruscant (system)
        ));

        // Carbon Chamber Testing / My Favorite Decoration (7_296)
        OBJECTIVE_REQUIREMENTS.put("7_296", Arrays.asList(
            "5_166",   // Carbonite Chamber
            "5_107",   // Carbonite Chamber Console
            "5_172"    // Security Tower
        ));

        // Court Of The Vile Gangster / I Shall Enjoy Watching You Die (110_6)
        OBJECTIVE_REQUIREMENTS.put("110_6", Arrays.asList(
            "6_162",   // Audience Chamber
            "6_170",   // Great Pit Of Carkoon
            "6_164"    // Dungeon
        ));

        // Watch Your Step / This Place Can Be A Little Rough (10_26)
        OBJECTIVE_REQUIREMENTS.put("10_26", Arrays.asList(
            "1_290",   // Tatooine: Cantina (Dark)
            "1_291",   // Tatooine: Docking Bay 94 (Dark)
            "1_289"    // Tatooine (Dark system)
        ));

        // Invasion / In Complete Control (14_113)
        OBJECTIVE_REQUIREMENTS.put("14_113", Arrays.asList(
            "12_169",     // Naboo (Dark system)
            "14_111",     // Blockade Flagship
            "14_96",      // Droid Racks
            "title:Swamp"  // Swamp location
        ));

        // Hunt Down And Destroy The Jedi (V) (213_31)
        OBJECTIVE_REQUIREMENTS.put("213_31", Arrays.asList(
            "209_50",         // Vader's Castle
            "213_16",         // Visage Of The Emperor (V)
            "title:Cloud City"  // Any Cloud City site with 1 dark icon
        ));

        // Shadow Collective / You Know Who I Answer To (213_32)
        OBJECTIVE_REQUIREMENTS.put("213_32", Collections.singletonList(
            "213_23"   // Maul's Chambers
        ));

        // I Want That Map / And Now You'll Give It To Me (208_57)
        OBJECTIVE_REQUIREMENTS.put("208_57", Arrays.asList(
            "204_53",  // Tuanul Village
            "208_40"   // I Will Finish What You Started
        ));

        // The Shield Will Be Down In Moments / Imperial Troops Have Entered (222_14)
        OBJECTIVE_REQUIREMENTS.put("222_14", Arrays.asList(
            "3_148",   // Hoth: Ice Plains (5th Marker)
            "3_149",   // Hoth: North Ridge (4th Marker)
            "222_9",   // Hoth: Main Power Generators (1st Marker) [Set 22]
            "13_82"    // Prepare For A Surface Attack
        ));

        // The Shield Will Be Down In Moments (AI) (222_30)
        OBJECTIVE_REQUIREMENTS.put("222_30", Arrays.asList(
            "3_148",   // Hoth: Ice Plains (5th Marker)
            "3_149",   // Hoth: North Ridge (4th Marker)
            "222_9",   // Hoth: Main Power Generators (1st Marker) [Set 22]
            "13_82"    // Prepare For A Surface Attack
        ));

        // The First Order Reigns / The Resistance Is Doomed (225_32)
        OBJECTIVE_REQUIREMENTS.put("225_32", Arrays.asList(
            "225_15",  // Crait (system)
            "211_19",  // D'Qar (system)
            "225_28",  // Supremacy: Bridge
            "225_34"   // Tracked Fleet
        ));
    }

    // =========================================================================
    // Instance Fields
    // =========================================================================

    private String objectiveBlueprintId = null;
    private List<String> requiredCards = new ArrayList<>();
    private Set<String> deployedRequirements = new HashSet<>();

    /**
     * Set the active objective and load its requirements.
     */
    public void setObjective(String blueprintId) {
        this.objectiveBlueprintId = blueprintId;
        this.requiredCards = OBJECTIVE_REQUIREMENTS.getOrDefault(blueprintId, Collections.emptyList());
        this.deployedRequirements = new HashSet<>();

        if (!requiredCards.isEmpty()) {
            LOG.info("Objective {} loaded with {} requirements", blueprintId, requiredCards.size());
        } else {
            LOG.debug("No special requirements for objective {}", blueprintId);
        }
    }

    /**
     * Score a card for starting card selection based on objective requirements.
     *
     * @param blueprintId The blueprint ID of the card to evaluate
     * @param cardTitle The title of the card
     * @return Score bonus (0 if not required, positive if required)
     */
    public float scoreStartingCard(String blueprintId, String cardTitle) {
        if (requiredCards.isEmpty()) {
            return 0.0f;
        }

        // Check if this card matches any requirement
        for (String req : requiredCards) {
            // Skip already deployed requirements
            if (deployedRequirements.contains(req)) {
                continue;
            }

            if (matchesRequirement(blueprintId, cardTitle, req)) {
                LOG.info("{} matches objective requirement: {}", cardTitle, req);
                deployedRequirements.add(req);
                return 200.0f;  // High bonus for objective-required cards
            }
        }

        return 0.0f;
    }

    /**
     * Check if a card matches an objective requirement.
     */
    private boolean matchesRequirement(String blueprintId, String cardTitle, String requirement) {
        // Exact blueprint match
        if (requirement.equals(blueprintId)) {
            return true;
        }

        // Title prefix match
        if (requirement.startsWith("title:")) {
            String reqTitle = requirement.substring(6).toLowerCase(Locale.ROOT);
            if (cardTitle != null && cardTitle.toLowerCase(Locale.ROOT).contains(reqTitle)) {
                return true;
            }
        }

        // Characteristic match (for battleground types, etc.)
        if (requirement.startsWith("characteristic:")) {
            // This would require card metadata lookup
            // For now, skip characteristic matching
        }

        return false;
    }

    /**
     * Get list of requirements not yet deployed.
     */
    public List<String> getRemainingRequirements() {
        List<String> remaining = new ArrayList<>();
        for (String req : requiredCards) {
            if (!deployedRequirements.contains(req)) {
                remaining.add(req);
            }
        }
        return remaining;
    }

    /**
     * Check if all objective requirements have been deployed.
     */
    public boolean isObjectiveStarted() {
        if (requiredCards.isEmpty()) {
            return true;
        }
        return deployedRequirements.size() >= requiredCards.size();
    }

    /**
     * Reset handler for a new game.
     */
    public void reset() {
        objectiveBlueprintId = null;
        requiredCards = new ArrayList<>();
        deployedRequirements = new HashSet<>();
    }

    /**
     * Get the current objective blueprint ID.
     */
    public String getObjectiveBlueprintId() {
        return objectiveBlueprintId;
    }

    /**
     * Check if an objective is currently loaded.
     */
    public boolean hasObjective() {
        return objectiveBlueprintId != null;
    }

    /**
     * Get all required cards for the current objective.
     */
    public List<String> getRequiredCards() {
        return Collections.unmodifiableList(requiredCards);
    }
}
