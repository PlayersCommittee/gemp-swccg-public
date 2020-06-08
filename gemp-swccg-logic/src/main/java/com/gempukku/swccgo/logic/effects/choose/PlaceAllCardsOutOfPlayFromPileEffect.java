package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect to place cards out of play all cards from the specified card pile.
 */
abstract class PlaceAllCardsOutOfPlayFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private String _cardPileOwner;

    /**
     * Creates an effect that causes the player to place cards out of play from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to take cards from
     * @param cardPileOwner the card pile owner
     */
    protected PlaceAllCardsOutOfPlayFromPileEffect(Action action, String playerId, Zone cardPile, String cardPileOwner) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(_action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardPile = gameState.getCardPile(_cardPileOwner, _cardPile);
                        if (!cardPile.isEmpty()) {
                            subAction.appendEffect(
                                    new PlaceCardsOutOfPlayFromOffTableEffect(subAction, cardPile));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
