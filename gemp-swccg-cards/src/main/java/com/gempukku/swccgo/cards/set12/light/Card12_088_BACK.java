package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Objective
 * Title: Plead My Case To The Senate / Sanity And Compassion
 */
public class Card12_088_BACK extends AbstractObjective {
    public Card12_088_BACK() {
        super(Side.LIGHT, 7, Title.Sanity_And_Compassion);
        setGameText("While this side up, once during your control phase may use 3 Force to place up to two random cards from opponent's hand into their Used Pile. Once per turn may take a Political Effect into hand from Reserve Deck; reshuffle. Your senators are destiny +2 (or +3 for any character with an order agenda) when drawn for battle or weapon destiny. May use 2 Force at end of a turn to place all cards on your Political Effects into Used Pile. Flip this card if you have less than two senators at Galactic Senate.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 3)) {
            final int maxCards = Math.min(2, GameConditions.numCardsInHand(game, opponent));
            if (maxCards > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place cards from opponent's hand in Used Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                if (maxCards > 1) {
                    // Choose target(s)
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose number of cards to place in Used Pile", 1, maxCards, maxCards) {
                                        @Override
                                        public void decisionMade(final int result) throws DecisionResultInvalidException {
                                            action.setActionMsg("Place " + result + " random card" + GameUtils.s(result) + " from opponent's hand in Used Pile");
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 3));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PutRandomCardsFromHandOnUsedPileEffect(action, playerId, opponent, 0, maxCards));
                                        }
                                    }
                            )
                    );
                }
                else {
                    action.setActionMsg("Place 1 random card from opponent's hand in Used Pile");
                    // Pay cost(s)
                    action.appendCost(
                            new UseForceEffect(action, playerId, 3));
                    // Perform result(s)
                    action.appendEffect(
                            new PutRandomCardFromHandOnUsedPileEffect(action, playerId, opponent));
                }
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.SANITY_AND_COMPASSION__UPLOAD_POLITICAL_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Political Effect into hand from Reserve Deck");
            action.setActionMsg("Take a Political Effect into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Political_Effect, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourSenator = Filters.and(Filters.your(self), Filters.senator, Filters.not(Filters.order_agenda));
        Filter yourCharacterWithOrderAgenda = Filters.and(Filters.your(self), Filters.character, Filters.order_agenda);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourSenator, 2));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourCharacterWithOrderAgenda, 3));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, yourSenator, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, yourCharacterWithOrderAgenda, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                && GameConditions.canUseForce(game, playerId, 2)) {
            Collection<PhysicalCard> cardsOnYourPoliticalEffects = Filters.filterStacked(game,
                    Filters.stackedOn(self, Filters.and(Filters.your(self), Filters.Political_Effect)));
            if (!cardsOnYourPoliticalEffects.isEmpty()) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place cards on your Political Effects into Used Pile");
                action.setActionMsg("Place cards on " + playerId + "'s Political Effects into Used Pile");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                // Perform result(s)
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, playerId, cardsOnYourPoliticalEffects, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(self), Filters.senator, Filters.at(Filters.Galactic_Senate)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}