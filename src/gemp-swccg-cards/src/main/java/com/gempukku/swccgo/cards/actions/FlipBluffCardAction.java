package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.FlipOverBluffCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to flip a 'bluff card' at the location.
 */
public class FlipBluffCardAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _effectPerformed;

    /**
     * Creates an action to flip a 'bluff card' at the location.
     * @param playerId the player flipping the 'bluff card'
     * @param location the location
     */
    public FlipBluffCardAction(String playerId, PhysicalCard location) {
        super(location, playerId);
        _location = location;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _location;
    }

    @Override
    public String getText() {
        return "Flip over a 'bluff card' here";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_effectPerformed) {
                _effectPerformed = true;
                return new FlipOverBluffCardEffect(this, _location);
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
