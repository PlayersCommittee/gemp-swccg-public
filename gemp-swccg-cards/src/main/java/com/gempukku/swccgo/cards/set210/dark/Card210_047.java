package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.InterruptPlacedOutOfPlayWhenCompletedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Insignificant Rebellion (V)
 */
public class Card210_047 extends AbstractNormalEffect {
    public Card210_047() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Insignificant_Rebellion, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'");
        setGameText("If Ralltiir Operations on table, deploy on table. Once per battle, when you draw battle destiny, may exchange a card in hand with a card of same card type in Lost Pile. If your Lost Interrupt (except Ghhhk) just resolved, place it out of play. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_10);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Ralltiir_Operations);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new InterruptPlacedOutOfPlayWhenCompletedModifier(self, Filters.and(Filters.your(self), Filters.Interrupt, Filters.not(Filters.Ghhhk)), CardSubtype.LOST));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.INSIGNIFICANT_REBELLION__LOST_PILE_SWAP;
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange a card in hand with a card in Lost Pile");

            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new ChooseCardFromHandEffect(action, playerId) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            Set<CardType> cardTypes = game.getModifiersQuerying().getCardTypes(game.getGameState(), selectedCard);
                            Filter filterForCardInLostPile = Filters.none;
                            for (CardType cardType : cardTypes) {
                                filterForCardInLostPile = Filters.or(filterForCardInLostPile, Filters.type(cardType));
                            }
                            action.appendEffect(new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, selectedCard, filterForCardInLostPile));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}