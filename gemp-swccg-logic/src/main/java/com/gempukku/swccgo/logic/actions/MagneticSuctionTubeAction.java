package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

public class MagneticSuctionTubeAction extends AbstractAction {
    private PhysicalCard _sourceCard;
    private PhysicalCard _magneticSuctionTube;
    private Filter _targetFilter;

    /**
     * Creates an action for using a magnetic suction tube.
     *
     * @param source  the card to initiate the firing
     * @param magneticSuctionTube  the magnetic suction tube
     * @param targetFilter  the filter for which cards can be targeted
     */
    public MagneticSuctionTubeAction(PhysicalCard source, PhysicalCard magneticSuctionTube, Filter targetFilter) {
        super();
        _sourceCard = source;
        _magneticSuctionTube = magneticSuctionTube;
        _targetFilter = targetFilter;
    }

    @Override
    public Action.Type getType() {
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

    @Override
    public Effect nextEffect(final SwccgGame game) {
        return null;
    }
}
