package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;

/**
 * The abstract class providing the common implementation for Starting Effects.
 */
public abstract class AbstractStartingEffect extends AbstractEffect {

    /**
     * Creates a blueprint for a Starting Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractStartingEffect(Side side, float destiny, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, Uniqueness.UNIQUE, expansionSet, rarity);
        setCardSubtype(CardSubtype.STARTING);
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
    public final boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
    }
}
