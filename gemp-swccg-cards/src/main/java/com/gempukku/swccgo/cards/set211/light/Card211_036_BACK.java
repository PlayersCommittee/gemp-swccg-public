package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Objective
 * Title: The Galaxy May Need A Legend / We Need Luke Skywalker
 */
public class Card211_036_BACK extends AbstractObjective {
    public Card211_036_BACK() {
        super(Side.LIGHT, 7, Title.We_Need_Luke_Skywalker);
        setGameText("Immediately place Luke out of play (ignore [Death Star II] objective restrictions, if any). For remainder of battle, opponent may not fire weapons. \n" +
                "While this side up, opponent's immunity to attrition is limited to < 5. Your Force drains are +1 where you have two unique (•) Resistance characters. Once during your turn, may peek at the top card of your Force Pile and Reserve Deck; place both cards (in any order) on top of one of those piles. Once per turn during battle, may cancel an opponent's just drawn destiny to cause a re-draw.");
        addIcons(Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Luke out of play.");
            action.setActionMsg("Place Luke out of play.");

            // Perform result(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Place Luke Out of Play", TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.Luke) {
                        //Immune To BHBM. See Card9_151. (Line 80)
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " out of play",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard luke = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardOutOfPlayFromTableEffect(action, luke));
                                        }
                                    });
                        }
                    });
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotBeFiredModifier(self, Filters.and(Filters.opponents(playerId), Filters.any)), "Prevents opponent's weapons from being fired")
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.THE_GALAXY_MAY_NEED_A_LEGEND_FORCE_PILE_UPLOAD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId1)
                && self.getWhileInPlayData() == null) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take a card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(new WhileInPlayData(true));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
                && GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final Collection<PhysicalCard> topCards = new LinkedList<>();
            final Collection<Zone> pilesCardsFrom = new LinkedList<>();

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Peek at top card of Force Pile and Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new PeekAtTopCardsOfCardPileEffect(action, playerId, playerId, Zone.FORCE_PILE,1));
            topCards.add(game.getGameState().getTopOfForcePile(playerId));
            pilesCardsFrom.add(Zone.FORCE_PILE);
            action.appendEffect(
                    new PeekAtTopCardsOfCardPileEffect(action, playerId, playerId, Zone.RESERVE_DECK,1));
            topCards.add(game.getGameState().getTopOfReserveDeck(playerId));
            pilesCardsFrom.add(Zone.RESERVE_DECK);
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Choose pile to put cards on top of", new String[]{"Reserve Deck", "Force Pile"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    final Zone zone;
                                    if (index == 0) {
                                        game.getGameState().sendMessage(playerId + " chooses Reserve Deck");
                                        zone = Zone.RESERVE_DECK;
                                    } else {
                                        game.getGameState().sendMessage(playerId + " chooses Force Pile");
                                        zone = Zone.FORCE_PILE;
                                    }
                                    game.getUserFeedback().sendAwaitingDecision(playerId,
                                            new ArbitraryCardsSelectionDecision("Choose order to place cards on top of " + zone.getHumanReadable() , topCards, topCards, 1, 1) {
                                                @Override
                                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                                    final PhysicalCard cardToPlaceInCardPile = getSelectedCardsByResponse(result).get(0);
                                                    final Zone fromCardPile = GameUtils.getZoneFromZoneTop(cardToPlaceInCardPile.getZone());
                                                    final String cardPileOwnerText = playerId + "s ";

                                                    String msgText = playerId + " places top card of " + cardPileOwnerText + cardToPlaceInCardPile.getZone().getHumanReadable() + " on " + cardPileOwnerText + zone.getHumanReadable();
                                                    action.appendEffect(
                                                            new PutOneCardFromCardPileInCardPileEffect(action, cardToPlaceInCardPile, fromCardPile, zone, playerId, false, msgText) {
                                                                @Override
                                                                protected void scheduleNextStep() {
                                                                    topCards.remove(cardToPlaceInCardPile);
                                                                    pilesCardsFrom.remove(fromCardPile);
                                                                    String msgText = playerId + " places top card of " + cardPileOwnerText + pilesCardsFrom.iterator().next().getHumanReadable() + " on " + cardPileOwnerText + zone.getHumanReadable();
                                                                    action.appendEffect(
                                                                            new PutOneCardFromCardPileInCardPileEffect(action, topCards.iterator().next(), pilesCardsFrom.iterator().next(), zone, playerId, false, msgText) {
                                                                                @Override
                                                                                protected void scheduleNextStep() {
                                                                                    topCards.remove(topCards.iterator().next());
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );

                                }
                            }
                    )
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter cardsWithMoreThan5ITA = Filters.and(Filters.opponents(self.getOwner()), Filters.immunityToAttritionLessThan(5));
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, cardsWithMoreThan5ITA, 5));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.location,
                Filters.hasDifferentCardTitlesAtLocation(self, Filters.and(Filters.your(self), Filters.unique, Filters.Resistance_character))), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId)))
                && GameConditions.isOncePerTurn(game, self, playerId,gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}