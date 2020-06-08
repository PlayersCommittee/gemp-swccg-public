package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;

public class DefeatedResult extends EffectResult {
    private PhysicalCard _cardDefeated;
    private Collection<PhysicalCard> _defeatedByCards;

    public DefeatedResult(PhysicalCard cardDefeated, Collection<PhysicalCard> defeatedByCards, String performingPlayer) {
        super(Type.DEFEATED, performingPlayer);
        _cardDefeated = cardDefeated;
        _defeatedByCards = new ArrayList<>(defeatedByCards);
    }

    public PhysicalCard getCardDefeated() {
        return _cardDefeated;
    }

    public Collection<PhysicalCard> getDefeatedByCards() {
        return _defeatedByCards;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_cardDefeated) + " just defeated";
    }
}
