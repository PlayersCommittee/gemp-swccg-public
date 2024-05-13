package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to place all cards in sabacc hand in Used Pile.
 */
public class PlaceSabaccHandInUsedPileEffect extends PlaceCardsInCardPileFromSabaccHandEffect {

    /**
     * Creates an effect that causes the player to place all cards in sabacc hand in Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param game the game
     */
    public PlaceSabaccHandInUsedPileEffect(Action action, String playerId, SwccgGame game) {
        super(action, playerId, playerId, game.getGameState().getSabaccHand(playerId), Zone.USED_PILE, playerId, false);
    }
}
