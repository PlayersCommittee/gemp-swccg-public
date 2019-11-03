package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 12
 * Type: Effect
 * Title: Insignificant Rebellion (V)
 */
public class Card501_016 extends AbstractNormalEffect {
    public Card501_016() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Insignificant_Rebellion, Uniqueness.UNIQUE);
        setLore("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'");
        setGameText("Deploy on table if Ralltiir Operations on table. Once per battle, when you draw battle destiny, may exchange a card in hand with a card of same card type in Lost Pile. Do They Have A Code Clearance? does not modify Imperials' forfeit values. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_12);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
        setTestingText("Insignificant Rebellion (V)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Do_They_Have_A_Code_Clearance), ModifyGameTextType.DO_THEY_HAVE_A_CODE_CLEARANCE__DOESNT_MODIFY_FORFEIT));
        return modifiers;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Ralltiir_Operations);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.INSIGNIFICANT_REBELLION__LOST_PILE_SWAP;
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getDarkPlayer())
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            action.setText("Exchange a card in hand with a card in Lost Pile");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new ChooseCardFromHandEffect(action, playerId) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            Set<CardType> cardTypes = selectedCard.getBlueprint().getCardTypes();
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