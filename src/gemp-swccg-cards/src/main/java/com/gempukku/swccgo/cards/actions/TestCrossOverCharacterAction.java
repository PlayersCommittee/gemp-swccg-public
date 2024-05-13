package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.timing.Effect;

public class TestCrossOverCharacterAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _character;
    private boolean _characterCrossedOver;
    private Effect _effect;

    public TestCrossOverCharacterAction(String playerId, final PhysicalCard character) {
        super(character, playerId);
        _character = character;

        _effect = new CrossOverCharacterEffect(this, character);
    }

    @Override
    public PhysicalCard getActionSource() {
        return _character;
    }

    @Override
    public String getText() {
        return "TEST: 'cross-over'";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_characterCrossedOver) {
                _characterCrossedOver = true;

                return _effect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _characterCrossedOver;
    }
}
