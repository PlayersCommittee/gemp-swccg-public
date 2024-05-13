package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to stack a 'bluff card' at the location.
 */
public class StackBluffCardAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _effectPerformed;

    /**
     * Creates an action to stack a 'bluff card' at the location.
     * @param playerId the player stacking the 'bluff card'
     * @param location the location
     */
    public StackBluffCardAction(String playerId, PhysicalCard location) {
        super(location, playerId);
        _location = location;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _location;
    }

    @Override
    public String getText() {
        return "Place 'bluff card' here";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_effectPerformed) {
                _effectPerformed = true;
                return new StackCardFromHandEffect(this, getPerformingPlayer(), _location, Filters.any, true, false, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose 'bluff card' to place under " + GameUtils.getFullName(_location);
                    }
                };
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
