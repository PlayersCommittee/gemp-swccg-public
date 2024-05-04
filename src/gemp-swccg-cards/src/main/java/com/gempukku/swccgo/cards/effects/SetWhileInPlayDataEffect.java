package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;


public class SetWhileInPlayDataEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private WhileInPlayData _data;

    public SetWhileInPlayDataEffect(Action action, PhysicalCard card, WhileInPlayData data) {
        super(action);
        _card = card;
        _data = data;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        _card.setWhileInPlayData(_data);
    }
}
