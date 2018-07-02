package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used as the effect that is responded when multiple cards are being deployed simultaneously for purposes of
 * canceling, re-targeting, etc.
 */
public abstract class RespondableDeployMultipleCardsSimultaneouslyEffect extends RespondableEffect implements RespondableDeployAsReactEffect {
    private PhysicalCard _cardPlayed1;
    private Zone _playedFromZone1;
    private PhysicalCard _playedFromStackedOn1;
    private PhysicalCard _cardPlayed2;
    private Zone _playedFromZone2;
    private PhysicalCard _playedFromStackedOn2;
    private boolean _asReact;

    /**
     * Creates a respondable play card effect for when multiple cards are being deployed simultaneously.
     * @param action the action performing this effect
     * @param targetingAction the action with the targeting information
     * @param cardPlayed1 the card being played
     * @param playedFromZone1 the zone the card was played from
     * @param playedFromStackedOn1 the card the played card was stacked on
     * @param cardPlayed2 the card being played as attached to cardPlayed1
     * @param playedFromZone2 the zone the card being played as attached to cardPlayed1 was played from
     * @param playedFromStackedOn2 the card the played being played as attached to cardPlayed1 card was stacked on
     * @param asReact true if the card is played as a react, otherwise false
     */
    public RespondableDeployMultipleCardsSimultaneouslyEffect(Action action, Action targetingAction, PhysicalCard cardPlayed1,
                                                              Zone playedFromZone1, PhysicalCard playedFromStackedOn1,
                                                              PhysicalCard cardPlayed2, Zone playedFromZone2,
                                                              PhysicalCard playedFromStackedOn2, boolean asReact) {
        super(action, targetingAction);
        _cardPlayed1 = cardPlayed1;
        _playedFromZone1 = playedFromZone1;
        _playedFromStackedOn1 = playedFromStackedOn1;
        _cardPlayed2 = cardPlayed2;
        _playedFromZone2 = playedFromZone2;
        _playedFromStackedOn2 = playedFromStackedOn2;
        _asReact = asReact;
    }

    @Override
    public Type getType() {
        return _asReact ? Type.PLAYING_CARDS_EFFECT : null;
    }

    /**
     * Determines if the card is being played as a 'react'.
     * @return true or false
     */
    public boolean isAsReact() {
        return _asReact;
    }

    /**
     * Gets the card that is deploying as a 'react'.
     * @return the card
     */
    @Override
    public PhysicalCard getCard1() {
        return _cardPlayed1;
    }

    /**
     * Gets the zone that the card is deploying from as a 'react'.
     * @return the zone
     */
    @Override
    public Zone getFromZone1() {
        return _playedFromZone1;
    }

    /**
     * Gets the card that the card deploying as a 'react' was stacked on, or null.
     * @return the card stacked on, or null
     */
    @Override
    public PhysicalCard getFromStackedOn1() {
        return _playedFromStackedOn1;
    }

    /**
     * Gets the card that is deploying simultaneously with the card that is deploying as a 'react', or null.
     * @return the card, or null
     */
    @Override
    public PhysicalCard getCard2() {
        return _cardPlayed2;
    }

    /**
     * Gets the zone that the simultaneously deploying card is deploying from.
     * @return the zone
     */
    @Override
    public Zone getFromZone2() {
        return _playedFromZone2;
    }

    /**
     * Gets the card that the simultaneously deploying card was stacked on, or null.
     * @return the card stacked on, or null
     */
    @Override
    public PhysicalCard getFromStackedOn2() {
        return _playedFromStackedOn2;
    }
}
