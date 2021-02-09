package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PayExtraCostToFireWeaponEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collection;

/**
 * An action for using a tractor beam.
 */
public class TractorBeamAction extends AbstractAction {
    private PhysicalCard _sourceCard;
    private PhysicalCard _tractorBeam;
    private Filter _targetFilter;
    private int _forceCost;
    private boolean _forFree;
    private int _destiniesToDraw;
    private Statistic _compareDestinyTo;

    /**
     * Creates an action for using a tractor beam.
     *
     * @param source  the card to initiate the firing
     * @param tractorBeam  the tractor beam
     * @param targetFilter  the filter for which cards can be targeted
     * @param forceCost  amount of Force to use
     * @param forFree   use it for free
     */
    public TractorBeamAction(PhysicalCard source, PhysicalCard tractorBeam, Filter targetFilter, int forceCost, boolean forFree, int destiniesToDraw, Statistic compareDestinyTo) {
        super();
        _sourceCard = source;
        _tractorBeam = tractorBeam;
        _targetFilter = targetFilter;
        _forceCost = forceCost;
        _forFree = forFree;
        _destiniesToDraw = destiniesToDraw;
        _compareDestinyTo = compareDestinyTo;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _sourceCard;
    }

    @Override
    public String getText() {
        return null;
    }

    public Filter getPossibleTargets() {
        return _targetFilter;
    }

    public int getForceCost() {
        return _forceCost;
    }

    public boolean getForFree() {
        return _forFree;
    }

    public int getNumDestinies() {
        return _destiniesToDraw;
    }

    @Override
    public Effect nextEffect(final SwccgGame game) {
        return null;
    }
}
