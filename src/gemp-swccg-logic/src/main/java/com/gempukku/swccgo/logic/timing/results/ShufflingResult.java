package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card pile is shuffled.
 */
public class ShufflingResult extends EffectResult {
    private PhysicalCard _source;
    private String _playerId;
    private String _zoneOwner;
    private Zone _cardPile;
    private boolean _pileChangedBeforeShuffle;

    /**
     * Creates an effect result that is triggered when a card pile is shuffled.
     * @param source the source card
     * @param playerId the player shuffling the card pile
     * @param zoneOwner the card pile owner
     * @param cardPile the card pile
     * @param pileChangedBeforeShuffle true if the card pile was changed (i.e. cards added/removed) before the shuffling was performed.
     */
    public ShufflingResult(PhysicalCard source, String playerId, String zoneOwner, Zone cardPile, boolean pileChangedBeforeShuffle) {
        super(Type.SHUFFLE_CARD_PILE, playerId);
        _source = source;
        _playerId = playerId;
        _zoneOwner = zoneOwner;
        _cardPile = cardPile;
        _pileChangedBeforeShuffle = pileChangedBeforeShuffle;
    }

    /**
     * Gets the source card of the shuffling, or null if the card pile was shuffled by game rule.
     * @return the source card, or null
     */
    public PhysicalCard getSource() {
        return _source;
    }

    /**
     * Gets the player that shuffled the card pile.
     * @return the player
     */
    public String getPlayerId() {
        return _playerId;
    }

    /**
     * Gets the owner of the card pile.
     * @return the owner
     */
    public String getZoneOwner() {
        return _zoneOwner;
    }

    /**
     * Gets the card pile.
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Determines if the card pile was changed before performing the shuffle.
     * @return true or false
     */
    public boolean isPileChangedBeforeShuffle() {
        return _pileChangedBeforeShuffle;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just shuffled " + getCardPile().getHumanReadable();
    }
}
