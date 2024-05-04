package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a Space Slug mouth is closed.
 */
public class CloseSpaceSlugMouthResult extends EffectResult {
    private PhysicalCard _spaceSlug;

    /**
     * Creates an effect result that is emitted when a Space Slug mouth is closed.
     * @param spaceSlug the Space Slug
     * @param playerId the performing player
     */
    public CloseSpaceSlugMouthResult(PhysicalCard spaceSlug, String playerId) {
        super(Type.CLOSE_SPACE_SLUG_MOUTH, playerId);
        _spaceSlug = spaceSlug;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Closed " + GameUtils.getCardLink(_spaceSlug) + " mouth";
    }
}
