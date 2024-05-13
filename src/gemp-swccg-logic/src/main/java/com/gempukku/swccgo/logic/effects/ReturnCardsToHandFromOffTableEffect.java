package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards not on table (e.g. in a card pile, etc.) to be returned to hand.
 */
public class ReturnCardsToHandFromOffTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified cards on table to be returned to hand.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public ReturnCardsToHandFromOffTableEffect(Action action, Collection<PhysicalCard> cards) {
        super(action);
        _cards.addAll(cards);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        if (!_cards.isEmpty()) {
            subAction.appendEffect(
                    new ReturnCardsToHandFromOffTableSimultaneouslyEffect(subAction, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
