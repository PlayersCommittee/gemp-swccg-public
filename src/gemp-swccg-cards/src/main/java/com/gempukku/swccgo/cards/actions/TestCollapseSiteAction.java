package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.CollapseSiteEffect;
import com.gempukku.swccgo.logic.timing.Effect;

public class TestCollapseSiteAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _site;
    private boolean _siteCollapsed;
    private Effect _effect;

    public TestCollapseSiteAction(String playerId, final PhysicalCard site) {
        super(site, playerId);
        _site = site;

        _effect = new CollapseSiteEffect(this, site);
    }

    @Override
    public PhysicalCard getActionSource() {
        return _site;
    }

    @Override
    public String getText() {
        return "TEST: 'collapse site'";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_siteCollapsed) {
                _siteCollapsed = true;

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
        return _siteCollapsed;
    }
}
