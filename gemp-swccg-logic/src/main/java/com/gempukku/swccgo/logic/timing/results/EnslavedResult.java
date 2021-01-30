package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card becomes 'enslaved'.
 */
public class EnslavedResult extends EffectResult {
    private PhysicalCard _enslavedCard;

    /**
     * Creates an effect result that is emitted when a card becomes 'enslaved'.
     * @param performingPlayerId the performing player
     * @param enslavedCard the enslaved card
     */
    public EnslavedResult(String performingPlayerId, PhysicalCard enslavedCard) {
        super(Type.CHARACTER_ENSLAVED, performingPlayerId);
        _enslavedCard = enslavedCard;
    }

    /**
     * Gets the card that was 'enslaved'.
     * @return the card
     */
    public PhysicalCard getEnslavedCard() {
        return _enslavedCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Enslaved' " + GameUtils.getCardLink(_enslavedCard);
    }
}
