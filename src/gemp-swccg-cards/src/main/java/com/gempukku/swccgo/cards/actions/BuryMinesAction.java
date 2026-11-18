package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * Top-level rule action to bury a card from hand face down under an exterior planet site
 * (Appendix C Mining Droid Rules — Burying Mines). Reuses under-site stacking like Bluff cards.
 */
public class BuryMinesAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _effectPerformed;

    public BuryMinesAction(String playerId, PhysicalCard location) {
        super(location, playerId);
        _location = location;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _location;
    }

    @Override
    public String getText() {
        return "Bury card under site";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_effectPerformed) {
                _effectPerformed = true;
                // faceDown, probe, bluff, combat, buriedMine, hidden
                return new StackCardFromHandEffect(this, getPerformingPlayer(), _location, Filters.any, true, false, false, false, true, true) {
                    @Override
                    public String getChoiceText() {
                        return "Choose card to bury under " + GameUtils.getFullName(_location);
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
