package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Effect
 * Subtype: Normal
 * Title: Eyes In The Dark (V)
 */
public class Card223_037 extends AbstractNormalEffect {
    public Card223_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Eyes_In_The_Dark, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setGameText("Deploy on table. If 4-LOM at a non-battleground, once during your turn, may peek at the top two cards of Reserve Deck and take one into hand. While you occupy two battlegrounds, opponent may not cancel your Force drains at battlegrounds.");
        addIcons(Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters._4_LOM, Filters.at(Filters.non_battleground_location)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at the top two cards of your Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.battleground, new OccupiesCondition(playerId, 2, Filters.battleground), opponent, playerId));
        return modifiers;
    }
}
