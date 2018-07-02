package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PurchaseResult;

import java.util.List;

public class PurchaseEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _cardToPurchase;

    public PurchaseEffect(Action action, PhysicalCard stolenCard) {
        this(action, action.getPerformingPlayer(), stolenCard);
    }

    public PurchaseEffect(Action action, String playerId, PhysicalCard cardToPurchase) {
        super(action);
        _playerId = playerId;
        _cardToPurchase = cardToPurchase;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), _cardToPurchase);
        if (location == null)
            return;

        List<PhysicalCard> allCardsPurchased = game.getGameState().getAttachedCards(_cardToPurchase, true);
        allCardsPurchased.add(0, _cardToPurchase);

        game.getGameState().sendMessage(_playerId + " 'purchases' " + GameUtils.getCardLink(_cardToPurchase) + " using " + GameUtils.getCardLink(_action.getActionSource()));

        // Update owner and zone owner of each card, then move just originally purchased card to location (since other cards are attached to it)
        for (PhysicalCard card : allCardsPurchased) {
            card.setOwner(_playerId);
            card.setZoneOwner(_playerId);
        }
        for (PhysicalCard card : allCardsPurchased) {
            game.getGameState().reapplyAffectingForCard(game, card);
        }

        game.getGameState().moveCardToLocation(_cardToPurchase, location);
        game.getActionsEnvironment().emitEffectResult(new PurchaseResult(_playerId, _cardToPurchase));
    }
}
