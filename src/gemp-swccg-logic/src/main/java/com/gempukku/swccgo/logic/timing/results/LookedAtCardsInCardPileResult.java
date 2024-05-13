package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered after a player looked at one or more cards in a card pile.
 */
public class LookedAtCardsInCardPileResult extends EffectResult {
    private String _zoneOwner;
    private Zone _cardPile;
    private PhysicalCard _source;


    /**
     * Creates an effect result that is triggered after a player looked at the cards in their own card pile.
     * @param playerId the player that looked at cards in their own pile
     * @param cardPile the card pile
     * @param source the source of the action
     */
    public LookedAtCardsInCardPileResult(String playerId, Zone cardPile, PhysicalCard source) {
        this(playerId, playerId, cardPile, source);
    }

    /**
     * Creates an effect result that is triggered after a player looked at the cards in a card pile.
     * @param playerId the player that looked at the cards
     * @param zoneOwner the zone owner
     * @param cardPile the card pile
     * @param source the source of the action
     */
    public LookedAtCardsInCardPileResult(String playerId, String zoneOwner, Zone cardPile, PhysicalCard source) {
        super(Type.LOOKED_AT_CARDS_IN_CARD_PILE, playerId);
        _zoneOwner = zoneOwner;
        _cardPile = cardPile;
        _source = source;
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
     * Gets the source of the action.
     * @return the source
     */
    public PhysicalCard getSource() {
        return _source;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return getPerformingPlayerId() + " just looked at one or more cards in " + _zoneOwner + "'s " + _cardPile.getHumanReadable();
    }
}
