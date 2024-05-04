package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that 'probes' the specified system by placing a 'probe' card from hand beneath it.
 */
public class ProbeSystemEffect extends AbstractSubActionEffect {
    private PhysicalCard _systemToProbe;

    /**
     * Creates an effect that 'probe' the specified system by placing a 'probe' card from hand beneath it.
     * @param action the action performing this effect
     * @param systemToProbe the system to 'probe'
     */
    public ProbeSystemEffect(Action action, PhysicalCard systemToProbe) {
        super(action);
        _systemToProbe = systemToProbe;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new StackCardFromHandEffect(subAction, subAction.getPerformingPlayer(), _systemToProbe, Filters.any, true, true, false, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose card to 'probe' with";
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
