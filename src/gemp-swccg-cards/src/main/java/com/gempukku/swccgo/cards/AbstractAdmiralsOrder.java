package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;

/**
 * The abstract class providing the common implementation for Admiral's Orders.
 */
public abstract class AbstractAdmiralsOrder extends AbstractNonLocationPlaysToTable {

    /**
     * Creates a blueprint for an Admiral's Order card.
     * @param side the side of the Force
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractAdmiralsOrder(Side side, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, 6f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, 0f, title, Uniqueness.UNIQUE, expansionSet, rarity);
        setCardCategory(CardCategory.ADMIRALS_ORDER);
        addCardType(CardType.ADMIRALS_ORDER);
        addIcon(Icon.ADMIRALS_ORDER);
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
                && Filters.canSpotFromTopLocationsOnTable(game, Filters.and(Filters.battleground_system, Filters.occupies(playerId)));
    }

    /**
     * Determines if the card can be played during the current phase.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card can be played during the current phase, otherwise false
     */
    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY);
    }

    /**
     * Determines if the card type, subtype, etc. always plays for free.
     * @return true if card type card type, subtype, etc. always plays for free, otherwise false
     */
    @Override
    protected final boolean isCardTypeAlwaysPlayedForFree() {
        return true;
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
        return true;
    }
}
