package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromCardPileResult;

import java.util.Collections;

/**
 * An effect that stacks a specified card from a card pile on a specified card.
 */
public class StackOneCardFromPileEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _card;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private boolean _reshuffle;
    private boolean _viaJediTest5;

    /**
     * Creates an effect that stacks a specified card from a card pile on a specified card.
     * @param action the action performing this effect
     * @param playerId the player to stack the card
     * @param card the card to stack
     * @param stackOn the card to stack the card on
     * @param faceDown true if card is stacked face down, otherwise false
     * @param reshuffle true if card pile is reshuffled after stacking card, otherwise false
     * @param viaJediTest5 true if stacked upside-down to be used as substitute destiny via Jedi Test #5, otherwise false
     */
    public StackOneCardFromPileEffect(Action action, String playerId, PhysicalCard card, PhysicalCard stackOn, boolean faceDown, boolean reshuffle, boolean viaJediTest5) {
        super(action);
        _playerId = playerId;
        _card = card;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _reshuffle = reshuffle;
        _viaJediTest5 = viaJediTest5;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        Zone cardPile = GameUtils.getZoneFromZoneTop(_card.getZone());
        String cardPileOwner = _card.getZoneOwner();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        boolean hidden = _faceDown;
        if (hidden) {
            hidden = !gameState.isCardPileFaceUp(_playerId, cardPile)
                    || (_card.getZone() != GameUtils.getZoneTopFromZone(cardPile));
        }
        String cardInfo = hidden ? "a card" : GameUtils.getCardLink(_card);
        String facing = _faceDown ? " face down" : "";
        String upsideDown = _viaJediTest5 ? " upside-down" : "";

        gameState.sendMessage(_playerId + " stacks " + cardInfo + " from " + cardPile.getHumanReadable() + facing + upsideDown + " on " + GameUtils.getCardLink(_stackOn));
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.stackCard(_card, _stackOn, _faceDown, false, _viaJediTest5);

        if (_reshuffle && gameState.getCardPileSize(cardPileOwner, cardPile) > 0) {
            gameState.shufflePile(cardPileOwner, cardPile);
            gameState.sendMessage(_playerId + " shuffles " + (_playerId.equals(cardPileOwner) ? "" : (cardPileOwner + "'s ")) + cardPile.getHumanReadable());
            game.getActionsEnvironment().emitEffectResult(new ShufflingResult(_action.getActionSource(), _playerId, cardPileOwner, cardPile, true));
        }
        else {
            game.getActionsEnvironment().emitEffectResult(new StackedFromCardPileResult(_action, _card, _stackOn));
        }
    }
}
