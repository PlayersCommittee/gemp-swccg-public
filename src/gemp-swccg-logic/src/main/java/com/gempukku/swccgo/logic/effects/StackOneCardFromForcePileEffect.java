package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;

public class StackOneCardFromForcePileEffect extends AbstractStandardEffect {
    private PhysicalCard _card;
    private PhysicalCard _stackOn;
    private boolean _faceDown;

    public StackOneCardFromForcePileEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown) {
        super(action);
        _card = card;
        _stackOn = stackOn;
        _faceDown = faceDown;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.FORCE_PILE;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            if (_faceDown)
                game.getGameState().sendMessage(_card.getOwner() + " stacks a card from Force Pile on " + GameUtils.getCardLink(_stackOn));
            else
                game.getGameState().sendMessage(_card.getOwner() + " stacks " + GameUtils.getCardLink(_card) + " from Force Pile on " + GameUtils.getCardLink(_stackOn));
            game.getGameState().removeCardsFromZone(Collections.singleton(_card));
            game.getGameState().stackCard(_card, _stackOn, _faceDown, false, false);
            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }
}
