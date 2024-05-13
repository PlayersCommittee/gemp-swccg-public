package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Zxyl Bes'uliik, Regent
 */
public class Card302_024 extends AbstractAlien {
    public Card302_024() {
        super(Side.DARK, 1, 5, 4, 4, 7,  "Zxyl Bes'uliik, Regent", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setArmor(5);
        setLore("Once a professional Bounty Hunter, Zxyl has settled into his role as Regent of the Brotherhood. Given his prestigious forging skills he's become a leader in the manufacturing industry.");
        setGameText("May use two different weapons. Once per turn may search reserve deck and take any device, weapon, vehicle, or starship found there into hand; reshuffle. ");
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.LEADER, Keyword.DARK_COUNCILOR);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.REGENT__TAKE_CARD_FROM_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a device, weapon, vehicle, or starship into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.device, Filters.starship, Filters.vehicle, Filters.starship, Filters.weapon), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
