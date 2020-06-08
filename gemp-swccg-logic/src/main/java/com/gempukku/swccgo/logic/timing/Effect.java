package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The interface which contains methods that all effects must implement.
 */
public interface Effect {
    /**
     * The effect types.
     */
    enum Type {
        RESPONDABLE_EFFECT,
        PLAYING_CARD_EFFECT,
        PLAYING_CARDS_EFFECT,
        MOVING_AS_REACT_USING_LANDSPEED,
        MOVING_AS_REACT_USING_HYPERSPEED,
        MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED,
        MOVING_AS_REACT_USING_SECTOR_MOVEMENT,
        LANDING_AS_REACT,
        TAKING_OFF_AS_REACT,
        ENTERING_STARSHIP_VEHICLE_SITE_AS_REACT,
        EXITING_STARSHIP_VEHICLE_SITE_AS_REACT,
        WEAPON_FIRING_EFFECT,
        REVEALING_INSERT_CARD,





        BEFORE_USE_FORCE,
        BEFORE_LOSE_FORCE_AS_COST,
        BEFORE_DRAW_CARD_FROM_FORCE_PILE,
        BEFORE_RELEASED,
        BEFORE_DRAW_DESTINY,
        BEFORE_TARGETING_ACTIVE_CARD_WITH_WEAPON,
        BEFORE_TARGETING_ACTIVE_CARD_WITH_REASON,
        BEFORE_TARGETING_ANY_CARD_WITH_WEAPON,
        BEFORE_TARGETING_ANY_CARD_WITH_REASON,
        BEFORE_FIRING_WEAPON,
        BEFORE_LOSING_ALL_PRESENT_WITH,
        BEFORE_LOOKING_AT_OPPONENTS_HAND,
        BEFORE_RETRIEVING_FORCE
    }

    /**
     * Returns the type of the effect. This should list the type of effect it represents
     * if the effect is a recognizable (and can be responded to) by the game. Otherwise, null.
     *
     * @return the type, or null
     */
    Type getType();

    /**
     * Returns the text that represents this effect. This text can be displayed on the User Interface if
     * there are triggers before the effect is carried out.
     *
     * @param game the game
     * @return the text
     */
    String getText(SwccgGame game);

    /**
     * Checks whether this effect can be played in full. This is required to check
     * for example for cards that give a choice of effects to carry out and one
     * that can be played in full has to be chosen.
     *
     * @param game the game
     * @return true if (based on current info) the effect should be able to be fully carried out, otherwise false
     */
    boolean isPlayableInFull(SwccgGame game);

    /**
     * Plays the effect and emits the results.
     *
     * @param game the game
     */
    void playEffect(SwccgGame game);

    /**
     * Returns the action this effect is a part of.
     *
     * @return the action
     */
    Action getAction();

    /**
     * Sets the action this effect is a part of. This is only used when returning effects from and action to
     * cover snapshots when effect is copied to a new action.
     **/
    void setAction(Action action);

    /**
     * Returns if the effect was carried out (not prevented) in full. This is required
     * for checking if effect that player can prevent by paying some cost should be
     * played anyway. If it was prevented, the original event has to be played.
     *
     * @return true if effect was fully carried out, otherwise false
     */
    boolean wasCarriedOut();

    /**
     * Cancels the effect and specifies the card that is the source of the canceling action.
     * @param canceledByCard the card
     */
    void cancel(PhysicalCard canceledByCard);

    /**
     * Determines if the effect is canceled.
     * @return true if the effect is canceled, otherwise false.
     */
    boolean isCanceled();

    /**
     * Gets the card that canceled the effect, or null if either the effect is not canceled or no source card was specified
     * when the effect was canceled.
     * @return the card that canceled the effect
     */
    PhysicalCard getCanceledByCard();
}
