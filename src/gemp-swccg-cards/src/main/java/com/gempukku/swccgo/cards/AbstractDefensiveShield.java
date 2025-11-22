package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * The abstract class providing the common implementation for Defensive Shields.
 */
public abstract class AbstractDefensiveShield extends AbstractNonLocationPlaysToTable {

    /**
     * Creates a blueprint for a Defensive Shield card.
     * @param side the side of the Force
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractDefensiveShield(Side side, PlayCardZoneOption playCardZoneOption, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, 0f, playCardZoneOption, 0f, title, Uniqueness.UNIQUE, expansionSet, rarity);
        setCardCategory(CardCategory.DEFENSIVE_SHIELD);
        addCardType(CardType.DEFENSIVE_SHIELD);
        addIcon(Icon.DEFENSIVE_SHIELD);
    }

    /**
     * Determines if the card type, subtype, etc. always plays for free.
     * @return true if card type, subtype, etc. always plays for free, otherwise false
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
        return false;
    }


    //because some shields (ex: Planetary Defense) play on locations (like an AbstractDeployable),
    //some calls need to be implemented that would cause crashes otherwise
    //see https://github.com/PlayersCommittee/gemp-swccg-public/issues/891
    /**
     * Gets the action to embark on a card (or to a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getEmbarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter moveTargetFilter) {
        return null;
    }

}
