package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a Big One: Asteroid Cave is changed to a Space Slug Belly (or vice versa).
 */
public class ChangeAsteroidCaveOrSpaceSlugBellyResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a Big One: Asteroid Cave is changed to a Space Slug Belly (or vice versa).
     * @param performingPlayer the player that performed the action
     * @param card the card whose game text is canceled
     */
    public ChangeAsteroidCaveOrSpaceSlugBellyResult(String performingPlayer, PhysicalCard card) {
        super(Type.CHANGED_ASTEROID_CAVE_OR_SPACE_SLUG_BELLY, performingPlayer);
        _card = card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        if (_card.isSpaceSlugBelly()) {
            return "Big One: Asteroid Cave changed to a Space Slug Belly";
        }
        else {
            return "Space Slug Belly changed to Big One: Asteroid Cave";
        }
    }
}
