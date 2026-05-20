package com.gempukku.swccgo.ai.models.chosenone;

/**
 * Configuration constants for The Chosen One AI.
 *
 * Based on Python bot config.py values. These are hardcoded constants
 * as agreed in the implementation plan - no external config files needed.
 */
public final class ChosenOneConfig {

    private ChosenOneConfig() {
        // Static constants only - no instantiation
    }

    // =========================================================================
    // Basic AI Settings
    // =========================================================================

    /** Hard cap - strongly avoid drawing above this */
    public static final int MAX_HAND_SIZE = 16;

    /** Soft cap - start penalizing draws above this */
    public static final int HAND_SOFT_CAP = 12;

    /** Random action chance (0-100) - adds some unpredictability */
    public static final int CHAOS_PERCENT = 0;  // Reduced from 25% for better strategic play

    // =========================================================================
    // Deploy Strategy
    // =========================================================================

    /**
     * Minimum TOTAL power we need to be able to deploy this turn before committing
     * characters to a location. Prevents deploying lone weak characters that get
     * overwhelmed. From C# deployThresholdSlider (typical value 6-8).
     */
    public static final int DEPLOY_THRESHOLD = 4;

    /** Power advantage where we stop reinforcing (overkill) */
    public static final int DEPLOY_OVERKILL_THRESHOLD = 8;

    /** Power advantage where reinforcing is low priority */
    public static final int DEPLOY_COMFORTABLE_THRESHOLD = 4;

    /** Force to reserve for initiating battle after deploy */
    public static final int BATTLE_FORCE_RESERVE = 1;

    /** Minimum score to deploy in turns 1-3 (hold back weak plays) */
    public static final int DEPLOY_EARLY_GAME_THRESHOLD = 110;

    /** How many turns count as "early game" */
    public static final int DEPLOY_EARLY_GAME_TURNS = 3;

    /** Enemy power threshold that prevents threshold relaxation (react/move threat) */
    public static final int REACT_THREAT_THRESHOLD = 8;

    /** Minimum power floor for establish/early game (weak char buddy) */
    public static final int MIN_ESTABLISH_POWER = 2;

    /** Maximum establish locations per plan (don't spread too thin) */
    public static final int MAX_ESTABLISH_LOCATIONS = 2;

    /** Target power at reinforced locations */
    public static final int REINFORCE_TARGET_POWER = 10;

    /** Low enemy power threshold for presence-only plans (stop bleeding) */
    public static final int LOW_ENEMY_THRESHOLD = 4;

    /** Weak character power threshold (needs buddy to deploy) */
    public static final int WEAK_CHARACTER_POWER = 3;

    /**
     * Minimum power a solo character must have to establish at an opponent-icon
     * location alone. Characters below this threshold must deploy as a group.
     * Prevents lone Jango (4), Mara (5) etc. from being left isolated where
     * the opponent can counter-deploy and initiate a winning battle.
     */
    public static final int MIN_SOLO_DEPLOY_POWER = 6;

    /** Power advantage beyond which we're safe (uncontested fortified) */
    public static final int UNCONTESTED_FORTIFIED_THRESHOLD = 6;

    /** Minimum force to consider deploying weapons */
    public static final int MIN_FORCE_FOR_WEAPONS = 3;

    /** Matching pilot power bonus */
    public static final int MATCHING_PILOT_BONUS = 3;

    /** Ability threshold for drawing battle destiny (SWCCG rule: need >= 4 at site) */
    public static final int ABILITY_THRESHOLD = 4;

    /** V33: Soft ability target — bonus for deploying to sites below this, penalty for moving away and dropping below */
    public static final int ABILITY_BUDDY_THRESHOLD = 7;

    /** Extra power needed to compensate for no destiny draw */
    public static final int ABILITY_POWER_COMPENSATION = 3;

    // =========================================================================
    // Force Economy
    // =========================================================================

    /** Target force generation (total icons) */
    public static final int FORCE_GEN_TARGET = 6;

    /** Maximum reserve deck peeks per turn */
    public static final int MAX_RESERVE_CHECKS = 2;

    // =========================================================================
    // Battle Strategy
    // =========================================================================

    /** Power advantage considered "good odds" for initiating battle */
    public static final int BATTLE_FAVORABLE_THRESHOLD = 4;

    /** Power disadvantage threshold to avoid/retreat from battle */
    public static final int BATTLE_DANGER_THRESHOLD = -6;

    /** Life force threshold for critical/desperate play */
    public static final int CRITICAL_LIFE_FORCE = 6;

    // =========================================================================
    // Scoring Weights
    // =========================================================================

    // Action scoring weights (used in evaluators)

    /** Base bonus for deploying to a contested location where we're losing */
    public static final int SCORE_REINFORCE_LOSING = 80;

    /** Base bonus for deploying to gain ground at opponent-controlled location */
    public static final int SCORE_GAIN_GROUND = 60;

    /** Penalty for deploying to 0-icon uncontested location */
    public static final int SCORE_PENALTY_ZERO_ICON_DEPLOY = -100;

    /** Bonus for deploying a matching pilot to their ship */
    public static final int SCORE_MATCHING_PILOT = 40;

    /** Bonus for deploying locations (opens deployment options) */
    public static final int SCORE_DEPLOY_LOCATION = 100;

    /** Bonus for Force drain action at controlled battleground */
    public static final int SCORE_FORCE_DRAIN = 120;

    /** Bonus for initiating favorable battle */
    public static final int SCORE_INITIATE_BATTLE = 80;

    /** Penalty for passing when we have good actions available */
    public static final int SCORE_PENALTY_PASS = 200;

    // =========================================================================
    // Priority Card Interaction
    // =========================================================================

    /** Score bonus for using damage cancel (Houjix/Ghhhk) when critical */
    public static final int SCORE_DAMAGE_CANCEL = 100;

    /** Score bonus for using barrier on high-power deployed card */
    public static final int SCORE_BARRIER_USE = 80;

    /** Score bonus for using Sense on high-value target */
    public static final int SCORE_SENSE_USE = 70;

    // =========================================================================
    // Chat Behavior
    // =========================================================================

    /** Whether to send chat messages */
    public static final boolean CHAT_ENABLED = true;

    /** Minimum seconds between chat messages */
    public static final int CHAT_MIN_INTERVAL_SECONDS = 3;

    /** Whether to limit chat to one message per turn */
    public static final boolean CHAT_LIMIT_ONE_PER_TURN = true;

    // =========================================================================
    // V35: Hunt Down Inquisitor + Hatred Strategy
    // =========================================================================

    /** Bonus for Vader deploying/moving to a location with Jedi */
    public static final int SCORE_VADER_SEEK_JEDI = 350;

    /** Bonus for hatred placement when Inquisitor is at opponent location */
    public static final int SCORE_HATRED_WITH_INQUISITOR = 400;

    /** Bonus for deploying Inquisitor to location with hatted opponent */
    public static final int SCORE_INQUISITOR_HATRED_SYNERGY = 300;

    /** Bonus for FMFTD lost mode with full synergy (Inquisitor+Jedi+Hatred) */
    public static final int SCORE_FMFTD_FULL_SYNERGY = 500;

    /** Vader expendability multiplier (1.0 = normal, 0.3 = expendable in Hunt Down) */
    public static final float VADER_EXPENDABILITY_FACTOR = 0.3f;
}
