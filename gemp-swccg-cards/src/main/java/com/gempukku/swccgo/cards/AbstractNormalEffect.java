package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * The abstract class providing the common implementation for Effects (without a subtype).
 */
public abstract class AbstractNormalEffect extends AbstractEffect {

    /**
     * Creates a blueprint for a normal Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractNormalEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype._);
    }

    /**
     * Gets the action for when 'insert' card is revealed.
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public final Action getInsertCardRevealedAction(SwccgGame game, PhysicalCard self) {
        return getGameTextInsertCardRevealed(game, self, self.getCardId());
    }

    /**
     * This method is overridden by individual cards to specify the action to perform when the card is an 'insert' card
     * that was just revealed.
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }
}
