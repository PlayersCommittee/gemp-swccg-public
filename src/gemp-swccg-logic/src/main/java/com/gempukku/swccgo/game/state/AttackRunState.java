package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the state information for an Epic Event action of Attack Run.
 */
public class AttackRunState extends EpicEventState {
    private PhysicalCard _leadStarfighter;
    private List<PhysicalCard> _wingmen = new ArrayList<PhysicalCard>();

    /**
     * Creates state information for an Epic Event action of Attack Run.
     * @param epicEvent the Epic Event card
     */
    public AttackRunState(PhysicalCard epicEvent) {
        super(epicEvent, Type.ATTACK_RUN);
    }

    /**
     * Sets the lead starfighter.
     * @param leadStarfighter the lead starfighter
     */
    public void setLeadStarfighter(PhysicalCard leadStarfighter) {
        _leadStarfighter = leadStarfighter;
    }

    /**
     * Gets the lead starfighter.
     * @return the lead starfighter
     */
    public PhysicalCard getLeadStarfighter() {
        return _leadStarfighter;
    }

    /**
     * Gets the wingmen.
     * @return the wingmen
     */
    public List<PhysicalCard> getWingmen() {
        return _wingmen;
    }
}
