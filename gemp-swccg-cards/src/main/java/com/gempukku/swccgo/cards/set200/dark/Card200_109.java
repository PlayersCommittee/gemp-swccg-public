package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndStackEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Coarse And Rough And Irritating (V)
 */
public class Card200_109 extends AbstractNormalEffect {
    public Card200_109() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Coarse And Rough And Irritating", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Tatooine's twin suns cause turbulent storms that strike with little or no warning. Strong winds whip rocks through the air with enormous force.");
        setGameText("Deploy on table; shuffle your Reserve Deck, peek at top two cards, and stack them face-up here. During battle, may take a card here into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). [Immune to Alter.]");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            // Perform result(s)
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndStackEffect(action, playerId, 2, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take stacked card into hand");
            action.setActionMsg("Prevent all battle destiny draws from being modified or canceled");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new TakeStackedCardIntoHandEffect(action, playerId, self));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotModifyBattleDestinyModifier(self), "Prevents all battle destiny draws from being modified"));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotCancelBattleDestinyModifier(self), "Prevents all battle destiny draws from being canceled"));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, playerId, 1));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}