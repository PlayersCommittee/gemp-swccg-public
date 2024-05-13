package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for Immediate Effects.
 */
public abstract class AbstractImmediateEffect extends AbstractEffect {

    /**
     * Creates a blueprint for an Immediate Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractImmediateEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.IMMEDIATE);
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
        return false;
    }
}
