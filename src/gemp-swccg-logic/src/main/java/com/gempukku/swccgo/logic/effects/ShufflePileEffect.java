package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;

/**
 * An effect that shuffles the specified card pile.
 */
public class ShufflePileEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _source;
    private String _playerId;
    private String _zoneOwner;
    private Zone _cardPile;
    private boolean _pileChangedBeforeShuffle;

    /**
     * Creates an effect that shuffles the specified card pile.
     * @param action the action performing this effect
     * @param zoneOwner the owner of the pile
     * @param cardPile the pile to shuffle
     */
    public ShufflePileEffect(Action action, String zoneOwner, Zone cardPile) {
        this(action, action.getActionSource(), action.getPerformingPlayer(), zoneOwner, cardPile, false);
    }

    /**
     * Creates an effect that shuffles the specified card pile.
     * @param action the action performing this effect
     * @param source the source card of the action
     * @param playerId the player shuffling the pile
     * @param zoneOwner the owner of the pile
     * @param cardPile the pile to shuffle
     * @param pileChangedBeforeShuffle true if the cards in the card pile changed before doing this shuffle
     */
    public ShufflePileEffect(Action action, PhysicalCard source, String playerId, String zoneOwner, Zone cardPile, boolean pileChangedBeforeShuffle) {
        super(action);
        _source = source;
        _playerId = playerId;
        _zoneOwner = zoneOwner;
        _cardPile = cardPile;
        _pileChangedBeforeShuffle = pileChangedBeforeShuffle;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Shuffle " + ((_playerId != null && !_playerId.equals(_zoneOwner)) ? "opponent's " : "") + _cardPile.getHumanReadable();
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (gameState.getCardPileSize(_zoneOwner, _cardPile) > 0) {
            gameState.sendMessage(_playerId + " shuffles " + (_playerId.equals(_zoneOwner) ? "" : (_zoneOwner + "'s ")) + _cardPile.getHumanReadable());
            gameState.shufflePile(_zoneOwner, _cardPile);
            game.getActionsEnvironment().emitEffectResult(new ShufflingResult(_source, _playerId, _zoneOwner, _cardPile, _pileChangedBeforeShuffle));
        }
    }
}
