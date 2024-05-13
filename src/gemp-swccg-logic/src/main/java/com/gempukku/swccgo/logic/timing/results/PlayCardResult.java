package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card was just played.
 */
public class PlayCardResult extends EffectResult {
    private PhysicalCard _playedCard;
    private Zone _playedFrom;
    private PhysicalCard _attachedTo;
    private PhysicalCard _atLocation;
    private PhysicalCard _toLocation;
    private boolean _asReact;
    private boolean _wasPlayedToSiteWithoutPresenceOrForceIcons;
    private boolean _wasPlayedToSitControlledByOpponent;
    private PhysicalCard _otherPlayedCard;
    private boolean _isDeploy;

    /**
     * Creates an effect result that is emitted when a card is played.
     * @param performingPlayerId the player that performed the action
     * @param playedCard the card played
     * @param playedFrom the zone the card was played from
     * @param attachedTo the card that played card was played as attached to
     * @param atLocation the location that the card was played to (if not attached to another card)
     * @param toLocation the location that the card was played to (even if attached to another card)
     * @param isDeploy true if playing card is a 'deploy' instead of 'play', other false
     * @param asReact true if deploying card as a 'react', otherwise false
     */
    public PlayCardResult(String performingPlayerId, PhysicalCard playedCard, Zone playedFrom, PhysicalCard attachedTo,
                          PhysicalCard atLocation, PhysicalCard toLocation, boolean isDeploy, boolean asReact) {
        super(Type.PLAY, performingPlayerId);
        _playedCard = playedCard;
        _playedFrom = playedFrom;
        _attachedTo = attachedTo;
        _atLocation = atLocation;
        _toLocation = toLocation;
        _isDeploy = isDeploy;
        _asReact = asReact;
    }

    /**
     * Gets the played card.
     * @return the played card
     */
    public PhysicalCard getPlayedCard() {
        return _playedCard;
    }

    /**
     * Gets the other played card, if another card was simultaneously played.
     * @return the other card
     */
    public PhysicalCard getOtherPlayedCard() {
        return _otherPlayedCard;
    }

    /**
     * Sets the other played card, if another card was simultaneously played.
     * @param otherPlayedCard the other card
     */
    public void setOtherPlayedCard(PhysicalCard otherPlayedCard) {
        _otherPlayedCard = otherPlayedCard;
    }

    /**
     * Gets the zone the played card was played from.
     * @return the zone
     */
    public Zone getPlayedCardFrom() {
        return _playedFrom;
    }

    /**
     * Gets the card the card played is attached to when played, or null if the card is not attached to another card.
     * @return the card the card is attached to.
     */
    public PhysicalCard getAttachedTo() {
        return _attachedTo;
    }

    /**
     * Gets the location the card played is at when played, or null if the card is not at the location (or attached to another card).
     * @return the location the card is at (without being attached to another card).
     */
    public PhysicalCard getAtLocation() {
        return _atLocation;
    }

    /**
     * Gets the location the card played is played to (even when attached to another card).
     * @return the location the card is played to (even when attached to another card).
     */
    public PhysicalCard getToLocation() {
        return _toLocation;
    }

    /**
     * Determines if card was 'deployed' instead of just 'played'.
     * @return true if deployed, false if played.
     */
    public boolean isDeployed() {
        return _isDeploy;
    }

    /**
     * Determines if card was deployed as a 'react'.
     * @return true if deployed as a 'react', otherwise false.
     */
    public boolean isAsReact() {
        return _asReact;
    }

    /**
     * Sets if the card was deployed to a site where the performing player did not have presence or Force icons.
     * @param value true, or false
     */
    public void setPlayedToSiteWithoutPresenceOrForceIcons(boolean value) {
        _wasPlayedToSiteWithoutPresenceOrForceIcons = value;
    }

    /**
     * Determines if the card was deployed to a site where the performing player did not have presence or Force icons.
     * @return true if player did not have presence or Force icons, otherwise false
     */
    public boolean isPlayedToSiteWithoutPresenceOrForceIcons() {
        return _wasPlayedToSiteWithoutPresenceOrForceIcons;
    }

    /**
     * Sets if the card was deployed to a site that was controlled by opponent.
     * @param value true, or false
     */
    public void setPlayedToSiteControlledByOpponent(boolean value) {
        _wasPlayedToSitControlledByOpponent = value;
    }

    /**
     * Determines if the card was deployed to a site that was controlled by opponent.
     * @return true if opponent controlled site, otherwise false
     */
    public boolean isPlayedToSiteControlledByOpponent() {
        return _wasPlayedToSitControlledByOpponent;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_playedCard) + " just " + (_isDeploy ? "deployed" : "played");
    }
}
