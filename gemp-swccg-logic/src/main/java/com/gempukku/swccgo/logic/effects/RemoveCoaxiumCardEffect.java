package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RemoveCoaxiumCardResult;

/**
 * An effect that removes a 'coaxium' card
 */
public class RemoveCoaxiumCardEffect extends AbstractSuccessfulEffect {

    private PhysicalCard _card;
    private Zone _cardPile;

    /**
     * An effect that removes a 'coaxium' card
     *
     * @param action   the action performing this effect
     * @param card     the card to remove
     * @param cardPile the card pile to place the card into it
     */
    public RemoveCoaxiumCardEffect(Action action, PhysicalCard card, Zone cardPile) {
        super(action);
        _card = card;
        _cardPile = cardPile;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String playerId = _action.getPerformingPlayer();
        _action.appendEffect(
                new PutStackedCardInUsedPileEffect(_action, playerId, _card, false)
        );
        game.getActionsEnvironment().emitEffectResult(new RemoveCoaxiumCardResult(playerId, _card, playerId, _cardPile));
    }
}
