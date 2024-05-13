package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;


public class ReturnCardToHandAction extends AbstractTopLevelRuleAction {
    private Effect _returnCardToHandEffect;
    private boolean _effectPerformed;

    public ReturnCardToHandAction(final PhysicalCard card, String playerId) {
        super(card, playerId);
        setPerformingPlayer(playerId);

        _returnCardToHandEffect = new ReturnCardToHandFromTableEffect(this, card);
    }

    @Override
    public String getText() {
        return "Return to hand";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_effectPerformed) {
                _effectPerformed = true;

                return _returnCardToHandEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _effectPerformed;
    }
}
