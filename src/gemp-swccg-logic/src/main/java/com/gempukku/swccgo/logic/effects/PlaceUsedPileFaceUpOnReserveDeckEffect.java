package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Places Used Pile face up on top of Reserve Deck, then shuffles (cut and replace).
 * Only cards moved from Used are marked face-up in Reserve; prior Reserve cards stay face-down.
 * Face-up marking is card-local (not isInserted). Cleared when the card leaves Reserve.
 */
public class PlaceUsedPileFaceUpOnReserveDeckEffect extends AbstractSuccessfulEffect {
    private final String _cardPileOwner;

    public PlaceUsedPileFaceUpOnReserveDeckEffect(Action action, String cardPileOwner) {
        super(action);
        _cardPileOwner = cardPileOwner;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Place Used Pile face up on Reserve Deck";
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        List<PhysicalCard> usedPile = gameState.getCardPile(_cardPileOwner, Zone.USED_PILE);
        if (usedPile.isEmpty()) {
            return;
        }

        List<PhysicalCard> movedFromUsed = new ArrayList<PhysicalCard>(usedPile);

        String playerNameForMsg = _action.getPerformingPlayer().equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ");
        gameState.sendMessage(_action.getPerformingPlayer() + " places " + playerNameForMsg + "Used Pile face up on " + playerNameForMsg + "Reserve Deck");
        gameState.placeCardPileOnCardPile(_cardPileOwner, Zone.USED_PILE, Zone.RESERVE_DECK);

        for (PhysicalCard card : movedFromUsed) {
            Zone zone = card.getZone();
            if (zone == Zone.RESERVE_DECK || zone == Zone.TOP_OF_RESERVE_DECK) {
                card.setFaceUpInReserveDeck(true);
            }
        }

        gameState.shuffleReserveDeck(_cardPileOwner);

        PhysicalCard top = gameState.getTopOfReserveDeck(_cardPileOwner);
        if (top != null && top.isFaceUpInReserveDeck()) {
            gameState.sendMessage(top.getOwner() + "'s " + top.getBlueprint().getTitle() + " is face up on top of Reserve Deck");
            gameState.setInsertCardFound(true);
            gameState.setSkipListenerUpdateAllowed(false);
        }

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, null, _cardPileOwner, Zone.RESERVE_DECK, false));
    }
}
