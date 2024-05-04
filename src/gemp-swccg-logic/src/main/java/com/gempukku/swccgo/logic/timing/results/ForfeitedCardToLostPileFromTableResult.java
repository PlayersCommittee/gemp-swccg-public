package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * This effect result is triggered when a card is forfeited to Lost Pile from table.
 */
public class ForfeitedCardToLostPileFromTableResult extends EffectResult implements LostFromTableResult {
    private PhysicalCard _card;
    private PhysicalCard _lostFromAttachedTo;
    private PhysicalCard _lostFromLocation;
    private Collection<PhysicalCard> _wasPresentWith;

    /**
     * Creates an effect result that is triggered when a card is forfeited to Lost Pile from table.
     * @param action the action performing this effect result
     * @param card the card
     * @param lostFromAttachedTo the card that the card was attached to when lost, or null
     * @param lostFromLocation the location the card was lost from, or null
     * @param wasPresentWith the cards that the card was present with when lost
     */
    public ForfeitedCardToLostPileFromTableResult(Action action, PhysicalCard card, PhysicalCard lostFromAttachedTo, PhysicalCard lostFromLocation, Collection<PhysicalCard> wasPresentWith) {
        this(action, action.getPerformingPlayer(), card, lostFromAttachedTo, lostFromLocation, wasPresentWith);
    }

    /**
     * Creates an effect result that is triggered when a card is place is forfeited to Lost Pile from table.
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     * @param lostFromAttachedTo the card that the card was attached to when lost, or null
     * @param lostFromLocation the location the card was lost from, or null
     * @param wasPresentWith the cards that the card was present with when lost
     */
    public ForfeitedCardToLostPileFromTableResult(Action action, String performingPlayerId, PhysicalCard card, PhysicalCard lostFromAttachedTo, PhysicalCard lostFromLocation, Collection<PhysicalCard> wasPresentWith) {
        super(Type.FORFEITED_TO_LOST_PILE_FROM_TABLE, performingPlayerId);
        _card = card;
        _lostFromAttachedTo = lostFromAttachedTo;
        _lostFromLocation = lostFromLocation;
        _wasPresentWith = wasPresentWith;
    }

    /**
     * Gets the card forfeited to Lost Pile from table.
     * @return the card
     */
    @Override
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the card the card was attached to when it was lost from table.
     * @return the card, or null if card was not attached to another card
     */
    @Override
    public PhysicalCard getFromAttachedTo() {
        return _lostFromAttachedTo;
    }

    /**
     * Gets the location the card was lost from.
     * @return the location, or null if card was not lost from a location
     */
    @Override
    public PhysicalCard getFromLocation() {
        return _lostFromLocation;
    }

    /**
     * Gets the cards that the card was present with when lost.
     * @return the cards that the cards was present with
     */
    @Override
    public Collection<PhysicalCard> getWasPresentWith() {
        return _wasPresentWith;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just forfeited " + GameUtils.getCardLink(_card);
    }
}
