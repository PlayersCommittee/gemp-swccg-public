package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;


public class ClearForRemainderOfGameDataEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private boolean _clearAll;

    public ClearForRemainderOfGameDataEffect(Action action, PhysicalCard card) {
        this(action, card, false);
    }

    public ClearForRemainderOfGameDataEffect(Action action, PhysicalCard card, boolean clearAll) {
        super(action);
        _card = card;
        _clearAll = clearAll;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_clearAll) {
            _card.clearForRemainderOfGameData();
        }
        else {
            _card.getForRemainderOfGameData().remove(_card.getCardId());
        }
    }
}
