package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TriggerAction;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract class representing results that an effect can produce. These are used as triggers that the game or other
 * cards the response to after an effect produced some result.
 */
public abstract class EffectResult implements Snapshotable<EffectResult> {
    private Set<String> _triggersUsed = new HashSet<String>();

    public enum Type {
        // Beginning of game steps
        STARTING_LOCATIONS_AND_OBJECTIVES_STEP_COMPLETE,

        // Phases of a turn
        START_OF_TURN,
        START_OF_PHASE,
        END_OF_PHASE,
        END_OF_TURN,

        // Play/deploy card
        PLAY,

        // Place card in play
        PLACE_IN_PLAY,

        // Character persona replaced
        PERSONA_REPLACED_CHARACTER,

        // Character converted
        CONVERT_CHARACTER,

        // Character crossed-over
        ABOUT_TO_CROSS_OVER,
        CROSSED_OVER_CHARACTER,

        // Location converted
        CONVERT_LOCATION,

        // Squadron replacement
        SQUADRON_REPLACEMENT,

        // Rotate card
        ROTATE_CARD,

        // Battle steps
        BEFORE_BATTLE_DESTINY_DRAWS,
        BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER,
        BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS,
        BATTLE_RESULT_DETERMINED,
        INITIAL_ATTRITION_CALCULATED,

        // Transferred
        TRANSFERRED_DEVICE_OR_WEAPON,

        // Epic event total
        CALCULATING_EPIC_EVENT_TOTAL,

        // Activate Force
        FORCE_ACTIVATED,

        // About to leave table
        ABOUT_TO_BE_CANCELED_ON_TABLE,
        ABOUT_TO_BE_LOST_FROM_TABLE,
        ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE,
        ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE,
        ABOUT_TO_BE_RETURNED_TO_HAND_FROM_TABLE,
        ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE,
        ABOUT_TO_BE_STACK_CARD_FROM_TABLE,

        // About to lose Force
        ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE,
        ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT,

        // About to draw destiny card
        ABOUT_TO_DRAW_DESTINY_CARD,

        // About to use card instead of normal destiny draw
        ABOUT_TO_USE_COMBAT_CARD_INSTEAD_OF_DESTINY_DRAW,

        // About to draw card from Force Pile
        ABOUT_TO_DRAW_CARD_FROM_FORCE_PILE,

        // About to remove just-lost card from Lost Pile
        ABOUT_TO_REMOVE_JUST_LOST_CARD_FROM_LOST_PILE,

        // Draw destiny
        COST_TO_DRAW_DESTINY_CARD,
        DESTINY_DRAWN,
        COMPLETE_DESTINY_DRAW,

        // Drawing destiny complete
        DRAWING_DESTINY_COMPLETE,

        // Force drain
        FORCE_DRAIN_INITIATED,
        FORCE_DRAIN_ENHANCED_BY_WEAPON,
        FORCE_DRAIN_COMPLETED,

        // Force retrieval
        FORCE_RETRIEVAL_INITIATED,
        FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE,
        RETRIEVED_FORCE,

        // Attack
        ATTACK_INITIATED,
        ATTACK_ENDED,
        ATTACK_CANCELED,

        // Battle
        BATTLE_INITIATED,
        BATTLE_WEAPONS_SEGMENT_COMPLETED,
        BATTLE_ENDING,
        BATTLE_ENDED,
        BATTLE_CANCELED,

        // Duel
        DUEL_INITIATED,
        DUEL_ADD_MODIFY_DUEL_DESTINIES_STEP,
        DUEL_RESULT_DETERMINED,
        DUEL_ENDING,
        DUEL_CANCELED,

        // Lightsaber combat
        LIGHTSABER_COMBAT_INITIATED,
        LIGHTSABER_COMBAT_ADD_MODIFY_LIGHTSABER_COMBAT_DESTINIES_STEP,
        LIGHTSABER_COMBAT_RESULT_DETERMINED,
        LIGHTSABER_COMBAT_ENDED,
        LIGHTSABER_COMBAT_CANCELED,

        // Podrace
        PODRACE_INITIATED,
        RACE_DESTINY_STACKED,
        PODRACER_DAMAGED,
        PODRACER_REPAIRED,
        PODRACE_FINISHED,

        // 'Blown away'
        BLOWN_AWAY_RELOCATE_STEP,
        BLOWN_AWAY_CALCULATE_FORCE_LOSS_STEP,
        BLOWN_AWAY_LAST_STEP,

        // Leaves table and/or card pile changed
        // Note: Escape option for Capturing also changes card piles
        REMOVE_COAXIUM_DESTINY,
        RETURNED_TO_HAND_FROM_TABLE,
        RETURNED_TO_HAND_FROM_OFF_TABLE,
        PUT_IN_RESERVE_DECK_FROM_TABLE,
        PUT_IN_FORCE_PILE_FROM_TABLE,
        PUT_IN_USED_PILE_FROM_TABLE,
        LOST_FROM_TABLE,
        PLACED_OUT_OF_PLAY_FROM_TABLE,
        STACKED_FROM_TABLE,
        CANCELED_ON_TABLE,
        PUT_TO_HAND_FROM_OFF_TABLE,
        PUT_IN_CARD_PILE_FROM_OFF_TABLE,
        PUT_IN_RESERVE_DECK_FROM_OFF_TABLE,
        PUT_IN_FORCE_PILE_FROM_OFF_TABLE,
        PUT_IN_USED_PILE_FROM_OFF_TABLE,
        ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_OFF_TABLE,
        PLACED_OUT_OF_PLAY_FROM_OFF_TABLE,
        LOST_FROM_OFF_TABLE,
        DRAW_CARD,
        FORCE_LOST,
        FORFEITED_TO_LOST_PILE_FROM_TABLE,
        FORFEITED_TO_USED_PILE_FROM_TABLE,
        FORFEITED_TO_LOST_PILE_FROM_OFF_TABLE,
        FORFEITED_TO_USED_PILE_FROM_OFF_TABLE,
        REMOVE_FROM_CARD_PILE,
        REMOVE_FROM_STACKED,
        SHUFFLE_CARD_PILE,
        EXCHANGE_WITH_CARD_PILE,
        REORDER_CARD_PILE,
        STACKED_FROM_CARD_PILE,
        STACKED_FROM_HAND,
        LOOKED_AT_OWN_CARD_PILE,

        // Canceling/restoring game text
        CANCELED_GAME_TEXT,
        RESTORED_GAME_TEXT,

        // Suspend/resume card
        SUSPEND_CARD,
        RESUME_CARD,

        // Binary turn off/on
        TURN_OFF_BINARY_DROID,
        TURN_ON_BINARY_DROID,

        // Verify card pile
        VERIFY_CARD_PILE,

        // Sabacc
        SABACC_TOTAL_CALCULATED,
        SABACC_WINNER_DETERMINED,

        // Card attribute changed
        ATTRIBUTE_RESET_OR_MODIFIED,
        FORFEIT_REDUCED_TO_ZERO,

        // Hit
        ABOUT_TO_BE_HIT,
        HIT,

        // Restored to normal
        RESTORED_TO_NORMAL,

        // Disarmed
        DISARMED,

        // Excluded from battle
        ABOUT_TO_HIDE_FROM_BATTLE,
        ABOUT_TO_BE_EXCLUDED_FROM_BATTLE,
        EXCLUDED_FROM_BATTLE,

        // Relocated from/to Weather Vane
        RELOCATED_TO_WEATHER_VANE,
        RELOCATED_FROM_LOST_IN_SPACE_OR_WEATHER_VANE_TO_LOCATION,

        // Weapon firing
        FIRED_WEAPON,

        // Stealing
        ABOUT_TO_BE_STOLEN,
        STOLEN,

        // Move
        MOVING_USING_LANDSPEED,
        MOVED_USING_LANDSPEED,
        MOVING_USING_HYPERSPEED,
        MOVED_USING_HYPERSPEED,
        MOVING_WITHOUT_USING_HYPERSPEED,
        MOVED_WITHOUT_USING_HYPERSPEED,
        MOVING_USING_SECTOR_MOVEMENT,
        MOVED_USING_SECTOR_MOVEMENT,
        LANDING,
        LANDED,
        TAKING_OFF,
        TOOK_OFF,
        MOVING_TO_START_BOMBING_RUN,
        MOVED_TO_START_BOMBING_RUN,
        MOVING_TO_END_BOMBING_RUN,
        MOVED_TO_END_BOMBING_RUN,
        MOVING_AT_START_OF_ATTACK_RUN,
        MOVED_AT_START_OF_ATTACK_RUN,
        MOVING_AT_END_OF_ATTACK_RUN,
        MOVED_AT_END_OF_ATTACK_RUN,
        MOVING_TO_RELATED_STARSHIP_OR_VEHICLE,
        MOVED_TO_RELATED_STARSHIP_OR_VEHICLE,
        MOVING_TO_RELATED_STARSHIP_OR_VEHICLE_SITE,
        MOVED_TO_RELATED_STARSHIP_OR_VEHICLE_SITE,
        ENTERING_STARSHIP_OR_VEHICLE_SITE,
        ENTERED_STARSHIP_OR_VEHICLE_SITE,
        EXITING_STARSHIP_OR_VEHICLE_SITE,
        EXITED_STARSHIP_OR_VEHICLE_SITE,
        EMBARKING,
        EMBARKED,
        DISEMBARKING,
        DISEMBARKED,
        CHANGED_CAPACITY_SLOT,
        SHUTTLING,
        SHUTTLED,
        SHIPDOCKED,
        TRANSFERRED_BETWEEN_DOCKED_STARSHIPS,
        MOVING_USING_LOCATION_TEXT,
        MOVED_USING_LOCATION_TEXT,
        DOCKING_BAY_TRANSITING,
        DOCKING_BAY_TRANSITED,
        RELOCATING_BETWEEN_LOCATIONS,
        RELOCATED_BETWEEN_LOCATIONS,
        MOVED_MOBILE_EFFECT,

        // Non-movement relocating
        ATTACH_FROM_TABLE,
        RELOCATE_TO_SIDE_OF_TABLE,

        // Cave Rules
        CHANGED_ASTEROID_CAVE_OR_SPACE_SLUG_BELLY,
        CLOSE_SPACE_SLUG_MOUTH,
        OPEN_SPACE_SLUG_MOUTH,

        // Undercover
        PUT_UNDERCOVER,
        COVER_BROKEN,

        // Missing
        GONE_MISSING,
        FOUND,

        // Capturing
        ABOUT_TO_BE_CAPTURED,
        FROZEN,
        CAPTURED,
        RELEASED,
        DELIVERED_CAPTIVE_TO_PRISON,
        TOOK_IMPRISONED_CAPTIVE_INTO_CUSTODY,
        LEFT_FROZEN_CAPTIVE_UNATTENDED,
        TOOK_UNATTENDED_FROZEN_CAPTIVE_INTO_CUSTODY,
        TRANSFERRED_CAPTIVE_TO_NEW_ESCORT,

        // Flip Double-Sided Card
        DOUBLE_SIDED_CARD_FLIPPED,

        // Jedi Testing
        JEDI_TEST_COMPLETED,

        // Creatures
        BULLSEYED,
        DEFEATED,
        EATEN,
        PARASITE_ATTACHED,
        PARASITE_DETACHED,

        // Probe
        PROBED_SYSTEM,
        PROBED_HIDDEN_BASE,

        // Sense/Alter Destiny Successful
        SENSE_ALTER_DESTINY_SUCCESSFUL,

        // 'Insert' card revealed
        INSERT_CARD_REVEALED,

        // Utinni Effect completed
        UTINNI_EFFECT_COMPLETED,

        // Collapsed site
        COLLAPSED_SITE,

        // Concealed
        CONCEALED,
        UNCONCEALED,

        // Exploding Program Trap
        EXPLODING_PROGRAM_TRAP,

        // Special Delivery
        SPECIAL_DELIVERY_COMPLETED,

        // Hypo question answered
        HYPO_QUESTION_ANSWERED,

        // Targeting
        RETARGETED_EFFECT,

        FORCE_USED,
        FOR_EACH_CRASHED,
        FOR_EACH_PURCHASED,

        // Dark Hours
        DARK_HOURS_EFFECT,


        IMMUNITY_GRANTED,

    }

    private Type _type;
    private String _performingPlayerId;
    private boolean _acceptingNoResponses;

    /**
     * Needed to generate snapshot.
     */
    public EffectResult() {
    }

    public void generateSnapshot(EffectResult selfSnapshot, SnapshotData snapshotData) {
        EffectResult snapshot = selfSnapshot;

        // Set each field
        snapshot._type = _type;
        snapshot._performingPlayerId = _performingPlayerId;
        snapshot._acceptingNoResponses = _acceptingNoResponses;
    }

    /**
     * Creates an effect result of the specified type and that was causes by an effect performed by the specified player.
     * @param type the type
     * @param performingPlayerId the player, or null if no specific player performed the effect
     */
    protected EffectResult(Type type, String performingPlayerId) {
        _type = type;
        _performingPlayerId = performingPlayerId;
    }

    /**
     * Gets the type.
     * @return the type
     */
    public Type getType() {
        return _type;
    }

    /**
     * Gets the performing player.
     * @return the performing player
     */
    public String getPerformingPlayerId() {
        return _performingPlayerId;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    public String getText(SwccgGame game) {
        return null;
    }

    /**
     * Determines if the player that is causing the effect result responds first. The default behavior is for the opponent
     * to response first.
     * @return true or false
     */
    public boolean isPerformingPlayerRespondsFirst() {
        return false;
    }

    /**
     * Records a trigger as having responded to this effect result.
     * A trigger is only allowed to respond to an effect result once.
     * @param action a trigger action that responded to this effect result
     */
    public void triggerUsed(TriggerAction action) {
        _triggersUsed.add(action.getTriggerIdentifier(false));
    }

    /**
     * Determines if the specified trigger has already responded to this effect result.
     * A trigger is only allowed to respond to an effect result once.
     * @param action a trigger action
     * @return true if the action has already responded, otherwise false
     */
    public boolean wasTriggerUsed(TriggerAction action) {
        return _triggersUsed.contains(action.getTriggerIdentifier(false));
    }

    /**
     * Sets that no more triggers can respond to this effect result.
     */
    public void acceptNoResponses() {
        _acceptingNoResponses = true;
    }

    /**
     * Determines if effect result accepts responses.
     * @return true if effect result is accepting responses, otherwise false
     */
    public boolean isAcceptingResponses() {
        return !_acceptingNoResponses;
    }
}
