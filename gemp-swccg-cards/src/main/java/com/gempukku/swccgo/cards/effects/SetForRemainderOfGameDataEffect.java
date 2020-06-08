package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;


public class SetForRemainderOfGameDataEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private ForRemainderOfGameData _data;
    private boolean _clearAll;

    public SetForRemainderOfGameDataEffect(Action action, PhysicalCard card, ForRemainderOfGameData data) {
        this(action, card, data, false);
    }

    public SetForRemainderOfGameDataEffect(Action action, PhysicalCard card, ForRemainderOfGameData data, boolean clearAll) {
        super(action);
        _card = card;
        _data = data;
        _clearAll = clearAll;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_clearAll) {
            _card.clearForRemainderOfGameData();
        }
        _card.setForRemainderOfGameData(_card.getCardId(), _data);
    }
}
