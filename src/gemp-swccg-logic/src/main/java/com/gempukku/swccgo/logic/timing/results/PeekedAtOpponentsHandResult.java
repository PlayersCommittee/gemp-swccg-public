package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Result emitted after a player peeks at opponent's hand (for example, with Radar Scanner).
 * Other cards such as Sensor Panel can respond without causing a second look at the hand.
 */
public class PeekedAtOpponentsHandResult extends EffectResult {
    private final PhysicalCard _source;
    private final List<PhysicalCard> _peekedAtCards;

    /**
     * @param playerId the player who peeked
     * @param source the card that caused the peek
     * @param peekedAtCards the cards that were seen
     */
    public PeekedAtOpponentsHandResult(String playerId, PhysicalCard source, List<PhysicalCard> peekedAtCards) {
        super(Type.PEEKED_AT_OPPONENTS_HAND, playerId);
        _source = source;
        _peekedAtCards = peekedAtCards != null ? peekedAtCards : Collections.emptyList();
    }

    public PhysicalCard getSource() {
        return _source;
    }

    public List<PhysicalCard> getPeekedAtCards() {
        return _peekedAtCards;
    }

    @Override
    public String getText(SwccgGame game) {
        return getPerformingPlayerId() + " just peeked at opponent's hand using " + GameUtils.getCardLink(_source);
    }
}
