package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is transferred between docked starships.
 */
public class TransferredBetweenDockedStarshipsResult extends EffectResult {
    private PhysicalCard _cardTransferred;

    /**
     * Creates an effect result that is emitted when a card is transferred between docked starships.
     * @param transferredCard the card that was transferred
     * @param playerId the performing player
     */
    public TransferredBetweenDockedStarshipsResult(PhysicalCard transferredCard, String playerId) {
        super(Type.TRANSFERRED_BETWEEN_DOCKED_STARSHIPS, playerId);
        _cardTransferred = transferredCard;
    }

    /**
     * Gets the card that was transferred.
     * @return the card that transferred
     */
    public PhysicalCard getTransferredCard() {
        return _cardTransferred;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Transferred " + GameUtils.getCardLink(_cardTransferred) + " between docked starships";
    }
}
