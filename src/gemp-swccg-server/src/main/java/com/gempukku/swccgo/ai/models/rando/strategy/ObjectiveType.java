package com.gempukku.swccgo.ai.models.rando.strategy;

/**
 * V40: Strategic classification of SWCCG objectives.
 *
 * Each type maps to a weight profile that adjusts evaluator behavior.
 * Objectives are classified at game start by ObjectiveTypeClassifier.
 */
public enum ObjectiveType {
    /** Win through combat damage — initiate battles, stack characters, destroy opponents.
     *  Examples: Hunt Down V, Set Your Course For Alderaan, TDIGWATT */
    AGGRESSIVE,

    /** Protect key locations, minimize losses, outlast opponent.
     *  Examples: ISB Operations, Quiet Mining Colony, Watch Your Step */
    DEFENSIVE,

    /** Win through force drains at multiple locations — spread presence widely.
     *  Examples: Profit (V), Hidden Base, No Money No Parts No Deal */
    DRAIN_FOCUSED,

    /** Space superiority — control systems, deploy starships, orbital battles.
     *  Examples: SYCFA, Ralltiir Operations, They Have No Idea */
    SPACE_CONTROL,

    /** Specific card combo win condition — get key cards on table in right order.
     *  Examples: Carbon Chamber Testing, Bring Him Before Me, Court Of The Vile Gangster */
    COMBO,

    /** Relies on unique high-ability characters as primary win condition.
     *  Examples: We'll Handle This, Throne Room Duel, Agents Of Black Sun */
    MAINS_HEAVY,

    /** Fallback for objectives not yet classified. Uses base weight profile. */
    GENERIC
}
