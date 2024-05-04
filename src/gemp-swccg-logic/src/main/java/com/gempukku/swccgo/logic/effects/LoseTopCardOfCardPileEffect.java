package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LostCardFromOffTableResult;

import java.util.Collections;

/**
 * An effect that causes the specified player to lose the top card of specified card pile.
 */
class LoseTopCardOfCardPileEffect extends AbstractStandardEffect {
    private String _playerId;
    private Zone _cardPile;
    private PhysicalCard _cardToLose;

    /**
     * Creates an effect that causes the specified player to lose the top card of specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile
     */
    protected LoseTopCardOfCardPileEffect(Action action, String playerId, Zone cardPile) {
        this(action, playerId, cardPile, null);
    }

    /**
     * Creates an effect that causes the specified player to lose the top card of specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile
     * @param cardToLose the specific card to lose
     */
    protected LoseTopCardOfCardPileEffect(Action action, String playerId, Zone cardPile, PhysicalCard cardToLose) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardToLose = cardToLose;
    }


    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getGameState().getTopOfCardPile(_playerId, _cardPile) != null;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard card = gameState.getTopOfCardPile(_playerId, _cardPile);
        if (card == null || (_cardToLose != null && (_cardToLose.getCardId() != card.getCardId())))
            return new FullEffectResult(false);

        Zone zone = card.getZone();

        // Places the card in the Lost Pile
        if (_cardPile.isLifeForce()
                && !gameState.isCardPileFaceUp(_playerId, _cardPile)
                && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.LIFE_FORCE_HIDDEN_WHEN_LOST, _playerId))
            gameState.sendMessage(_playerId + " loses a card from top of " + zone.getHumanReadable());
        else
            gameState.sendMessage(_playerId + " loses " + GameUtils.getCardLink(card) + " from top of " + zone.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(card));
        gameState.addCardToTopOfZone(card, Zone.LOST_PILE, _playerId);

        // Emits effect result that a card was just lost
        game.getActionsEnvironment().emitEffectResult(new LostCardFromOffTableResult(_action, _playerId, card));
        return new FullEffectResult(true);
    }
}
