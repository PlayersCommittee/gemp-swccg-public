package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
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
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutRandomCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PutRandomCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
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
 * Title: My Lord, Is That Legal? / I Will Make It Legal
 */
public class Card12_179_BACK extends AbstractObjective {
    public Card12_179_BACK() {
        super(Side.DARK, 7, Title.I_Will_Make_It_Legal, ExpansionSet.CORUSCANT, Rarity.U);
        setGameText("While this side up, once during your control phase may use 3 Force to place up to two random cards from opponent's hand into their Used Pile. Once per turn may take a Political Effect into hand from Reserve Deck; reshuffle. Your senators are destiny +2 (or +3 if senator has an ambition agenda) when drawn for weapon or battle destiny. May use 2 Force at end of any turn to place all cards on your Political Effects into Used Pile. Flip this card if you have less than two senators at Galactic Senate.");
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

        gameTextActionId = GameTextActionId.I_WILL_MAKE_IT_LEGAL__UPLOAD_POLITICAL_EFFECT;

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
        Filter yourSenatorWithoutAmbitionAgenda = Filters.and(Filters.your(self), Filters.senator, Filters.not(Filters.ambition_agenda));
        Filter yourSenatorWithAmbitionAgenda = Filters.and(Filters.your(self), Filters.senator, Filters.ambition_agenda);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourSenatorWithoutAmbitionAgenda, 2));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourSenatorWithAmbitionAgenda, 3));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, yourSenatorWithoutAmbitionAgenda, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, yourSenatorWithAmbitionAgenda, 3));
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