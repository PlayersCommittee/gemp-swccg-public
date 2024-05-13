package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.common.PlayCardActionReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used as the effect that is responded when a single card is being deployed for purposes of canceling, re-targeting, etc.
 */
public abstract class RespondableDeploySingleCardEffect extends RespondableEffect implements RespondablePlayingCardEffect, RespondableDeployAsReactEffect {
    private PhysicalCard _cardPlayed;
    private Zone _playedFromZone;
    private PhysicalCard _playedFromStackedOn;
    private boolean _asReact;
    private boolean _asInsertCard;

    /**
     * Creates a respondable play card effect for when a single card is being deployed.
     * @param action the action performing this effect
     * @param targetingAction the action with the targeting information
     * @param cardPlayed the card being played
     * @param playedFromZone the zone the card was played from
     * @param playedFromStackedOn the card the played card was stacked on
     * @param asReact true if the card is deployed as a react, otherwise false
     * @param asReact true if the card is deployed as an 'insert' card, otherwise false
     */
    public RespondableDeploySingleCardEffect(Action action, Action targetingAction, PhysicalCard cardPlayed, Zone playedFromZone,
                                             PhysicalCard playedFromStackedOn, boolean asReact, boolean asInsertCard) {
        super(action, targetingAction);
        _cardPlayed = cardPlayed;
        _playedFromZone = playedFromZone;
        _playedFromStackedOn = playedFromStackedOn;
        _asReact = asReact;
        _asInsertCard = asInsertCard;
    }

    @Override
    public Type getType() {
        return _asReact || !_cardPlayed.getBlueprint().isCardTypeMayNotBeCanceled() ? Type.PLAYING_CARD_EFFECT : null;
    }

    /**
     * Gets the card being played or deployed.
     * @return the card
     */
    @Override
    public PhysicalCard getCard() {
        return _cardPlayed;
    }

    /**
     * Gets the zone the card is being played or deployed from.
     * @return the zone
     */
    @Override
    public Zone getPlayingFromZone() {
        return _playedFromZone;
    }

    /**
     * Determines if the card is being deployed as a 'react'.
     * @return true or false
     */
    @Override
    public boolean isAsReact() {
        return _asReact;
    }

    /**
     * Determines if card is being deployed as an 'insert' card.
     * @return true or false
     */
    @Override
    public boolean isAsInsertCard() {
        return _asInsertCard;
    }

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    @Override
    public boolean isToBePlacedOutOfPlay() {
        return false;
    }

    /**
     * Determines if card is being played for the specified reason.
     * @return true or false
     */
    @Override
    public boolean isPlayingForReason(PlayCardActionReason actionReason) {
        return false;
    }

    /**
     * Gets the card that is deploying as a 'react'.
     * @return the card
     */
    @Override
    public PhysicalCard getCard1() {
        return _cardPlayed;
    }

    /**
     * Gets the zone that the card is deploying from as a 'react'.
     * @return the zone
     */
    @Override
    public Zone getFromZone1() {
        return _playedFromZone;
    }

    /**
     * Gets the card that the card deploying as a 'react' was stacked on, or null.
     * @return the card stacked on, or null
     */
    @Override
    public PhysicalCard getFromStackedOn1() {
        return _playedFromStackedOn;
    }

    /**
     * Gets the card that is deploying simultaneously with the card that is deploying as a 'react', or null.
     * @return the card, or null
     */
    @Override
    public PhysicalCard getCard2() {
        return null;
    }

    /**
     * Gets the zone that the simultaneously deploying card is deploying from.
     * @return the zone
     */
    @Override
    public Zone getFromZone2() {
        return null;
    }

    /**
     * Gets the card that the simultaneously deploying card was stacked on, or null.
     * @return the card stacked on, or null
     */
    @Override
    public PhysicalCard getFromStackedOn2() {
        return null;
    }
}
