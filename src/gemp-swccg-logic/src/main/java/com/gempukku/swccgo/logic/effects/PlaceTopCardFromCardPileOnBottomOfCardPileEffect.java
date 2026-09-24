package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

/**
 * An effect to place the top card of a card pile on bottom of a card pile.
 */
public class PlaceTopCardFromCardPileOnBottomOfCardPileEffect extends AbstractSuccessfulEffect {
    private String _cardPileOwner;
    private Zone _fromPile;
    private Zone _toPile;

    /**
     * Creates an effect to place the top card of a card pile on bottom of a card pile.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     * @param fromPile the card pile to take the card from
     * @param toPile the card pile to move the card to
     */
    public PlaceTopCardFromCardPileOnBottomOfCardPileEffect(Action action, String cardPileOwner, Zone fromPile, Zone toPile) {
        super(action);
        _cardPileOwner = cardPileOwner;
        _fromPile = fromPile;
        _toPile = toPile;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        // Slip Sliding Away: Frozen Assets is conceptual top of Force Pile when that pile is empty.
        // Unfreeze by returning frozen Force to Force Pile; Frozen Assets stays on Frozen Pile until end of turn.
        if (_fromPile == Zone.FORCE_PILE && _toPile == Zone.FORCE_PILE) {
            PhysicalCard top = gameState.getTopOfCardPile(_cardPileOwner, _fromPile);
            PhysicalCard topFrozen = gameState.getTopOfFrozenPile(_cardPileOwner);
            boolean faOnFrozenTop = topFrozen != null && com.gempukku.swccgo.filters.Filters.Frozen_Assets.accepts(game, topFrozen);
            boolean forcePileEmpty = top == null;
            if (faOnFrozenTop && forcePileEmpty) {
                int frozenCount = gameState.getFrozenForceCount(_cardPileOwner);
                gameState.moveFrozenPileToForcePile(_cardPileOwner);
                String playerNameForMsg = _action.getPerformingPlayer().equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ");
                gameState.sendMessage(_action.getPerformingPlayer() + " relocates Frozen Assets within " + playerNameForMsg
                        + "Force Pile; " + frozenCount + " frozen Force become usable");
                return;
            }
        }

        PhysicalCard card = gameState.getTopOfCardPile(_cardPileOwner, _fromPile);
        if (card == null)
            return;

        boolean hidden = (!game.getGameState().isCardPileFaceUp(_cardPileOwner, _fromPile) && !game.getGameState().isCardPileFaceUp(_cardPileOwner, _toPile));

        String cardInfo = hidden ? "a card" : GameUtils.getCardLink(card);
        String playerNameForMsg = _action.getPerformingPlayer().equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ");
        gameState.sendMessage(_action.getPerformingPlayer() + " places " + cardInfo + " from the top of " + playerNameForMsg + _fromPile.getHumanReadable() + " to the bottom of " + playerNameForMsg + _toPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(card));
        gameState.addCardToZone(card, _toPile, _cardPileOwner);

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, card, _cardPileOwner, _toPile, false));
    }
}
