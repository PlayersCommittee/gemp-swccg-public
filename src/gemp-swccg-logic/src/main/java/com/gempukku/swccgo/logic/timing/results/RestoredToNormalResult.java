package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect that is emitted when a card is restored to normal (removed 'hit', etc.).
 */
public class RestoredToNormalResult extends EffectResult {
    private PhysicalCard _restoredCard;
    private PhysicalCard _restoredByCard;

    /**
     * Creates an effect that is emitted when a card is restored to normal (removed 'hit', etc.).
     * @param performingPlayerId the player that performed the action
     * @param restoredCard the card that was restored
     * @param restoredByCard the card that restored the card
     */
    public RestoredToNormalResult(String performingPlayerId, PhysicalCard restoredCard, PhysicalCard restoredByCard) {
        super(Type.RESTORED_TO_NORMAL, performingPlayerId);
        _restoredCard = restoredCard;
        _restoredByCard = restoredByCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Restored " + GameUtils.getCardLink(_restoredCard);
    }
}
