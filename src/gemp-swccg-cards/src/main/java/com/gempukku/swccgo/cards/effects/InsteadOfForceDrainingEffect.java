package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An effect that performs another effect "instead of Force draining".
 */
public class InsteadOfForceDrainingEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;
    private StandardEffect _effectToPerform;

    /**
     * Creates an effect that performs another effect "instead of Force draining".
     * @param action the action performing this effect
     * @param location the Force drain location
     * @param effectToPerform the effect to perform "instead of Force draining"
     */
    public InsteadOfForceDrainingEffect(Action action, PhysicalCard location, StandardEffect effectToPerform) {
        super(action);
        _location = location;
        _effectToPerform = effectToPerform;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Record that something is being done "instead of Force draining"
                        game.getModifiersQuerying().forceDrainAttempted(_location);
                    }
                }
        );
        subAction.appendEffect(_effectToPerform);

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}