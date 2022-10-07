package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;

/**
 * The abstract class providing the common implementation for systems.
 */
public abstract class AbstractSystem extends AbstractLocation {
    private int _parsec;
    private String _systemOrbiting;

    /**
     * Creates a blueprint for a system.
     * @param side the side of the Force
     * @param title the card title
     * @param parsec the parsec number
     */
    protected AbstractSystem(Side side, String title, int parsec) {
        this(side, title, parsec, null);
    }

    /**
     * Creates a blueprint for a system.
     * @param side the side of the Force
     * @param title the card title
     * @param parsec the parsec number
     * @param systemOrbiting the system this must deploy orbiting
     */
    protected AbstractSystem(Side side, String title, int parsec, String systemOrbiting) {
        this(side, title, parsec, systemOrbiting, null, null);
    }

    /**
     * Creates a blueprint for a system.
     * @param side the side of the Force
     * @param title the card title
     * @param parsec the parsec number
     * @param systemOrbiting the system this must deploy orbiting
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractSystem(Side side, String title, int parsec, String systemOrbiting, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, Uniqueness.UNIQUE, expansionSet, rarity);
        _parsec = parsec;
        _systemOrbiting = systemOrbiting;
        setCardSubtype(CardSubtype.SYSTEM);
    }

    @Override
    public final int getParsec() {
        return _parsec;
    }

    @Override
    public final String getSystemName() {
        return getTitle();
    }

    @Override
    public final String getDeploysOrbitingSystem() {
        return _systemOrbiting;
    }

    /**
     * Determines if the card can be played.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param playCardOption the play card option, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card can be played, otherwise false
     */
    @Override
    protected boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)
                && (_systemOrbiting == null || Filters.canSpotFromTopLocationsOnTable(game, Filters.and(Filters.system, Filters.title(_systemOrbiting))));
    }
}
