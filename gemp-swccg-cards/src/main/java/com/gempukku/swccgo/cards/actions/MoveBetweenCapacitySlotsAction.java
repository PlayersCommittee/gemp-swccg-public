package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.MoveBetweenCapacitySlotsEffect;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * The action to move a character between pilot/driver and passenger slots, or move a card in cargo hold between
 * vehicle and starfighter slots.
 */
public class MoveBetweenCapacitySlotsAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private PhysicalCard _cardAboard;
    private Effect _changeCapacitySlotEffect;
    private boolean _cardMoved;

    /**
     * Create an action to move the specified card between capacity slots.
     * @param card the card
     */
    public MoveBetweenCapacitySlotsAction(final String playerId, final PhysicalCard card) {
        super(card, playerId);
        _cardToMove = card;
        _cardAboard = card.getAttachedTo();
        _changeCapacitySlotEffect = new MoveBetweenCapacitySlotsEffect(this, card);
    }

    @Override
    public String getText() {
        if (_cardToMove.isInCargoHoldAsVehicle()) {
            return "Move to starfighter capacity slot";
        }
        if (_cardToMove.isInCargoHoldAsStarfighterOrTIE()) {
            return "Move to vehicle capacity slot";
        }
        if (_cardToMove.isPassengerOf()) {
            if (_cardAboard.getBlueprint().getCardSubtype() == CardSubtype.TRANSPORT)
                return "Move to driver capacity slot";
            else
                return "Move to pilot capacity slot";
        }
        else {
            return "Move to passenger capacity slot";
        }
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!_cardMoved) {
            _cardMoved = true;
            return _changeCapacitySlotEffect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardMoved && _changeCapacitySlotEffect.wasCarriedOut();
    }
}
