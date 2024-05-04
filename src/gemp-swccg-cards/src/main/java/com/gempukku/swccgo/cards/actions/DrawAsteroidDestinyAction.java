package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DrawAsteroidDestinyEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to draw asteroid destiny against the starship at a location with "Asteroid Rules" in effect.
 */
public class DrawAsteroidDestinyAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _starship;
    private boolean _effectPerformed;

    /**
     * Creates an action to draw asteroid destiny against the starship at a location with "Asteroid Rules" in effect.
     * @param playerId the player drawing asteroid destiny
     * @param starship the starship
     */
    public DrawAsteroidDestinyAction(String playerId, PhysicalCard starship) {
        super(starship, playerId);
        _starship = starship;
    }

    @Override
    public String getText() {
        return "Draw asteroid destiny";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_effectPerformed) {
                _effectPerformed = true;
                return new DrawAsteroidDestinyEffect(this, getPerformingPlayer(), _starship);
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
