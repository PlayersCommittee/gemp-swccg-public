package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a player is about to draw a card from Force Pile.
 */
public class AboutToDrawCardFromForcePileResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when a player is about to draw a card from Force Pile.
     * @param playerId the player
    */
    public AboutToDrawCardFromForcePileResult(String playerId) {
        super(Type.ABOUT_TO_DRAW_CARD_FROM_FORCE_PILE, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to draw card from Force pile";
    }
}
