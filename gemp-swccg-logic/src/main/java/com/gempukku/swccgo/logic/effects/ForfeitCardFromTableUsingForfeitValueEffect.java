package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card on table to be forfeited using a specified forfeit value.
 */
public class ForfeitCardFromTableUsingForfeitValueEffect extends AbstractSubActionEffect {
    private PhysicalCard _card;
    private float _forfeitValue;
    private boolean _toUsedPile;

    /**
     * Creates an effect that causes the specified cards on table to be forfeited using a specified forfeit value.
     * @param action the action performing this effect
     * @param card the card
     * @param forfeitValue the forfeit value
     */
    public ForfeitCardFromTableUsingForfeitValueEffect(Action action, PhysicalCard card, float forfeitValue) {
        this(action, card, forfeitValue, false);
    }

    /**
     * Creates an effect that causes the specified cards on table to be forfeited using a specified forfeit value.
     * @param action the action performing this effect
     * @param card the card
     * @param forfeitValue the forfeit value
     * @param toUsedPile true if card is forfeited to Used Pile, otherwise false
     */
    public ForfeitCardFromTableUsingForfeitValueEffect(Action action, PhysicalCard card, float forfeitValue, boolean toUsedPile) {
        super(action);
        _card = card;
        _forfeitValue = forfeitValue;
        _toUsedPile = toUsedPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Check if card is forfeited to Used Pile
        boolean goesToUsedPile = _toUsedPile || game.getModifiersQuerying().isForfeitedToUsedPile(game.getGameState(), _card);

        // SubAction to carry out forfeiting card from table
        subAction.appendEffect(
                new ForfeitCardsFromTableSimultaneouslyEffect(subAction, Collections.singleton(_card), _forfeitValue, goesToUsedPile));

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
