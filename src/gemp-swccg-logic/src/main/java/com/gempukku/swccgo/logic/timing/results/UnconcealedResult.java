package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card detaches and becomes no longer 'concealed'.
 */
public class UnconcealedResult extends EffectResult {
    private PhysicalCard _unconcealedCard;

    /**
     * Creates an effect result that is emitted when a card detaches and becomes no longer 'concealed'.
     * @param performingPlayerId the performing player
     * @param unconcealedCard the card
     */
    public UnconcealedResult(String performingPlayerId, PhysicalCard unconcealedCard) {
        super(Type.UNCONCEALED, performingPlayerId);
        _unconcealedCard = unconcealedCard;
    }

    /**
     * Gets the card that is no longer 'concealed'.
     * @return the card
     */
    public PhysicalCard getUnconcealedCard() {
        return _unconcealedCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_unconcealedCard) + " no longer 'concealed'";
    }
}
