package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;


public class ClearTargetedCardsEffect extends AbstractSuccessfulEffect implements TargetingEffect {
    private PhysicalCard _cardTargeting;

    public ClearTargetedCardsEffect(Action action, PhysicalCard sourceCard) {
        super(action);
        _cardTargeting = sourceCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        _cardTargeting.clearTargetedCards();
    }
}
