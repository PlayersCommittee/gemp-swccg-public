package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for an Epic Event action of Target The Main Generator.
 */
public class TargetTheMainGeneratorState extends EpicEventState {
    private PhysicalCard _atat;
    private PhysicalCard _atatCannon;
    private PhysicalCard _pilot;

    /**
     * Creates state information for an Epic Event action of Target The Main Generator.
     * @param epicEvent the Epic Event card
     */
    public TargetTheMainGeneratorState(PhysicalCard epicEvent) {
        super(epicEvent, Type.TARGET_THE_MAIN_GENERATOR);
    }

    /**
     * Sets the AT-AT.
     * @param atat the AT-AT
     */
    public void setAtat(PhysicalCard atat) {
        _atat = atat;
    }

    /**
     * Gets the AT-AT.
     * @return the AT-AT
     */
    public PhysicalCard getAtat() {
        return _atat;
    }

    /**
     * Sets the AT-AT Cannon.
     * @param atatCannon the AT-AT Cannon
     */
    public void setAtatCannon(PhysicalCard atatCannon) {
        _atatCannon = atatCannon;
    }

    /**
     * Gets the AT-AT Cannon.
     * @return the AT-AT Cannon
     */
    public PhysicalCard getAtatCannon() {
        return _atatCannon;
    }

    /**
     * Sets the AT-AT pilot (or AT-AT itself if permanent pilot was chosen).
     * @param pilot the AT-AT pilot
     */
    public void setAtatPilot(PhysicalCard pilot) {
        _pilot = pilot;
    }

    /**
     * Gets the AT-AT pilot (or AT-AT itself if permanent pilot was chosen).
     * @return the AT-AT pilot
     */
    public PhysicalCard getAtatPilot() {
        return _pilot;
    }
}
