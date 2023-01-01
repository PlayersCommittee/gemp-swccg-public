package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo, Optimistic General
 */
public class Card215_011 extends AbstractRebel {
    public Card215_011() {
        super(Side.LIGHT, 1, 4, 4, 3, 6, "Han Solo, Optimistic General", Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLore("Leader. Scout.");
        setGameText("[Pilot] 3. Cancels Kylo's game text here. When deployed, may reveal the top two cards of your Reserve Deck; take one into hand. During battle with Chewie or [Endor] Leia, may add one destiny to total power.");
        addPersona(Persona.HAN);
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.ENDOR, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.LEADER, Keyword.SCOUT, Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Kylo, Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reveal top two cards of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardsOfReserveDeckEffect(action, playerId, 2) {
                        @Override
                        protected void cardsRevealed(final List<PhysicalCard> cards) {
                            if (cards.size() == 2) {
                                action.appendEffect(
                                        new ChooseArbitraryCardsEffect(action, playerId, "Choose card to take into hand", cards, 1, 1) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                PhysicalCard cardToTakeIntoHand = selectedCards.iterator().next();
                                                if (cardToTakeIntoHand != null) {
                                                    action.appendEffect(
                                                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardToTakeIntoHand, false));
                                                    Collection<PhysicalCard> nonSelectedCards = Filters.filter(cards, game, Filters.not(cardToTakeIntoHand));
                                                    PhysicalCard cardToPutBack = nonSelectedCards.iterator().next();
                                                    if (cardToPutBack != null) {
                                                        action.appendEffect(
                                                                new PutCardFromReserveDeckOnTopOfCardPileEffect(action, cardToPutBack, Zone.RESERVE_DECK, false));
                                                    }
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Chewie, Filters.and(Icon.ENDOR, Filters.Leia)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");

            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1, playerId));

            return Collections.singletonList(action);
        }

        return null;
    }
}