package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to release an 'unattended frozen' captive.
 */
public class ReleaseUnattendedFrozenCaptiveAction extends AbstractTopLevelRuleAction {
    private boolean _captiveReleased;
    private Effect _releaseCaptiveEffect;
    private Action _that;

    /**
     * Creates an action to release an 'unattended frozen' captive at the site.
     * @param playerId the performing player
     * @param site the site
     */
    public ReleaseUnattendedFrozenCaptiveAction(final String playerId, final PhysicalCard site) {
        super(site, playerId);
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose unattended frozen captive to release",
                        SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.unattendedFrozenCaptive, Filters.atLocation(site))) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _releaseCaptiveEffect = new ReleaseCaptiveEffect(_that, selectedCard);
                    }
                });
    }

    @Override
    public String getText() {
        return "Release an unattended frozen captive";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_captiveReleased) {
                _captiveReleased = true;
                return _releaseCaptiveEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _captiveReleased && _releaseCaptiveEffect.wasCarriedOut();
    }
}
