package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * This effect result is triggered when a card is canceled on table.
 */
public class CancelCardOnTableResult extends EffectResult implements LostFromTableResult {
    private PhysicalCard _sourceCard;
    private PhysicalCard _card;
    private PhysicalCard _attachedTo;

    /**
     * Creates an effect result that is triggered when a card canceled on table.
     * @param action the action performing this effect result
     * @param card the card
     * @param attachedTo the card the canceled card was attached to, or null
     */
    public CancelCardOnTableResult(Action action, PhysicalCard card, PhysicalCard attachedTo) {
        this(action, action.getPerformingPlayer(), card, attachedTo);
    }

    /**
     * Creates an effect result that is triggered when a card is canceled on table.
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     * @param attachedTo the card the canceled card was attached to, or null
     */
    public CancelCardOnTableResult(Action action, String performingPlayerId, PhysicalCard card, PhysicalCard attachedTo) {
        super(Type.CANCELED_ON_TABLE, performingPlayerId);
        _sourceCard = action.getActionSource();
        _card = card;
        _attachedTo = attachedTo;
    }

    /**
     * Gets the card that is the source of the cancel action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    /**
     * Gets the card canceled on table.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the card that the canceled card was attached to.
     * @return the card the canceled card was attached to, or null
     */
    public PhysicalCard getFromAttachedTo() {
        return _attachedTo;
    }

    /**
     * Gets the location the card was canceled from.
     * @return the location, or null if card was not lost from a location
     */
    @Override
    public PhysicalCard getFromLocation() {
        return null;
    }

    /**
     * Gets the cards that the card was present with when lost.
     * @return the cards that the cards was present with
     */
    @Override
    public Collection<PhysicalCard> getWasPresentWith() {
        return Collections.emptyList();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just canceled " + GameUtils.getCardLink(_card);
    }
}
