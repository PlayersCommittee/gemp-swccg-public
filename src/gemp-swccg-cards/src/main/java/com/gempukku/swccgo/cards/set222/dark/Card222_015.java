package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardTitleWithOwnerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: Alien
 * Title: Tonnika Sisters (V)
 */
public class Card222_015 extends AbstractAlien {
    public Card222_015() {
        super(Side.DARK, 2, 2, 2, 2, 2, Title.Tonnika_Sisters, Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setComboCard(true);
        setLore("Twins. Thieves. Con artists. Spies. Swindlers. Double agents. Brea and Senni use their natural charm to sway the unwary on the fringe of society.");
        setGameText("Assassins. Power and forfeit +2 at a cantina or night club. " +
                "Twice during battle, may double your just drawn battle destiny of 2. " +
                "Twice per game, if you have exactly two cards in hand, may draw two cards from Reserve Deck. Immune to Kiffex.");
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.SPY, Keyword.THIEF, Keyword.FEMALE, Keyword.ASSASSIN);
        addIcons(Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AtCondition atCantinaOrNightClub = new AtCondition(self, Filters.or(Filters.Cantina, Filters.Nightclub));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, atCantinaOrNightClub, 2));
        modifiers.add(new ForfeitModifier(self, atCantinaOrNightClub, 2));
        modifiers.add(new ImmuneToCardTitleWithOwnerModifier(self, Title.Kiffex, self.getOwner()));
        modifiers.add(new ImmuneToCardTitleWithOwnerModifier(self, Title.Kiffex, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isNumTimesPerBattle(game, self, 2, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)) {
            // Check more condition(s)
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            PhysicalCard drawnCard = destinyDrawnResult.getCard();
            if (drawnCard != null
                    && drawnCard.getDestinyValueToUse() == 2) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Double the destiny");
                // Perform result(s)
                action.appendEffect(
                        new ModifyDestinyEffect(action, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TONNIKA_SISTERS_V__DRAW_CARDS;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && game.getGameState().getReserveDeck(playerId).size() >= 2
                && game.getGameState().getHand(playerId).size() == 2) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two cards from Reserve Deck");
            action.setActionMsg("Draw two cards from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
