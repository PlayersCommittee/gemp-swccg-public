package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;



import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Rose Telsniw, Seer
 */
public class Card302_043 extends AbstractAlien {
    public Card302_043() {
        super(Side.LIGHT, 1, 4, 2, 4, 5, "Rose Telsniw, Seer", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Rose is a mysterious Seer working with the Children of Mortis. No one is quite certain of her true motivation for doing so.");
        setGameText("May reveal the top two cards of your Reserve Deck; take one into hand and place the other in Used Pile. Once during your turn, if at a battleground, may peek at top card of opponent's Reserve Deck. If it is a character, may reveal it and opponent loses 1 Force (if it is a Councilor, may also place it on Used Pile).");
        addKeywords(Keyword.CHILDREN_OF_MORTIS);
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
                                                    PhysicalCard cardToPlaceInUsedPile = nonSelectedCards.iterator().next();
                                                    if (cardToPlaceInUsedPile != null) {
                                                        action.appendEffect(
                                                                new PutCardFromReserveDeckOnTopOfCardPileEffect(action, cardToPlaceInUsedPile, Zone.USED_PILE, false));
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
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            if (Filters.character.accepts(game, peekedAtCard)) {
                                // Ask player about putting revealing card
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to reveal " + GameUtils.getCardLink(peekedAtCard) + " to make opponent lose 1 force?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.setActionMsg("Reveal " + GameUtils.getCardLink(peekedAtCard) + " to make opponent lose 1 force");
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RevealTopCardOfReserveDeckEffect(action, playerId, opponent));
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 1));
                                                        if (Filters.Dark_Councilor.accepts(game, peekedAtCard)) {
                                                            action.appendEffect(
                                                                    new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Place card on used pile?") {
                                                                        @Override
                                                                        protected void yes() {
                                                                            action.appendEffect(
                                                                                    new PutCardFromReserveDeckOnTopOfCardPileEffect(action, peekedAtCard, Zone.USED_PILE, false)
                                                                            );
                                                                        }
                                                                    })
                                                            );

                                                        }
                                                    }

                                                    protected void no() {
                                                        action.setActionMsg(null);
                                                        game.getGameState().sendMessage(playerId + " chooses to not reveal top card of opponent's Reserve Deck");
                                                    }
                                                }
                                        ));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
