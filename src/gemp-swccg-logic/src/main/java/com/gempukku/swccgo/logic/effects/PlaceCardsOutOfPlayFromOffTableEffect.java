package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect that causes the specified cards not on table (e.g. in a card pile, in hand, etc.) to be placed out of play.
 */
public class PlaceCardsOutOfPlayFromOffTableEffect extends AbstractSubActionEffect {
    private List<PhysicalCard> _cards = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified cards not on table (e.g. in a card pile, in hand, etc.) to be placed
     * out of play.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public PlaceCardsOutOfPlayFromOffTableEffect(Action action, Collection<PhysicalCard> cards) {
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
                    new PlaceCardsOutOfPlayFromOffTableSimultaneouslyEffect(subAction, _cards));
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            cardsPlacedOutOfPlay(_cards);
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A callback method for the cards placed out of play.
     * @param cards the cards placed out of play
     */
    protected void cardsPlacedOutOfPlay(List<PhysicalCard> cards) {
    }
}
