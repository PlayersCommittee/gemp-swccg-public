package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for an Epic Event action to attempt to 'blow away' Death Star II.
 */
public class AttemptToBlowAwayDeathStarIIState extends EpicEventState {
    private PhysicalCard _starfighter;
    private PhysicalCard _pilot;

    /**
     * Creates state information for an Epic Event action to attempt to 'blow away' Death Star II.
     * @param epicEvent the Epic Event card
     */
    public AttemptToBlowAwayDeathStarIIState(PhysicalCard epicEvent) {
        super(epicEvent, Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II);
    }

    /**
     * Sets the starfighter.
     * @param starfighter the starfighter
     */
    public void setStarfighter(PhysicalCard starfighter) {
        _starfighter = starfighter;
    }

    /**
     * Gets the starfighter.
     * @return the starfighter
     */
    public PhysicalCard getStarfighter() {
        return _starfighter;
    }

    /**
     * Sets the pilot (or starfighter itself if permanent pilot was chosen).
     * @param pilot the starfighter pilot
     */
    public void setPilot(PhysicalCard pilot) {
        _pilot = pilot;
    }

    /**
     * Gets the starfighter pilot (or starfighter itself if permanent pilot was chosen).
     * @return the starfighter pilot
     */
    public PhysicalCard getPilot() {
        return _pilot;
    }
}
