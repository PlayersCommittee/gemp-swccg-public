package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ExchangeCardFromHandWithStackedCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: Either Way, You Win (V)
 */
public class Card221_057 extends AbstractUsedInterrupt {
    public Card221_057() {
        super(Side.LIGHT, 4, "Either Way, You Win", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Deal!'");
        setGameText("Each player may choose to play a Defensive Shield from under their Starting Effect or to exchange a card in hand with one of their race destinies. [Immune to Sense.] OR If a battle involving Qui-Gon was just initiated at a junkyard, weapons may not be fired this battle.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Starting_Effect)
                || GameConditions.canSpot(game, self, Filters.raceDestiny)) {

            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
            final String opponent = game.getOpponent(playerId);
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);

            action.setImmuneTo(Title.Sense);
            action.setText("Allow each player to choose");
            // Allow response(s)
            action.allowResponses("Allow each player to choose to play a Defensive Shield from under their Starting Effect or to exchange a card in hand with one of their race destinies.",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    getEitherWayYouWinPlayoutDecisionEffect(game, self, action, playerId));
                            action.appendEffect(
                                    getEitherWayYouWinPlayoutDecisionEffect(game, self, action, opponent));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.titleContains("junkyard"))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.QuiGon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Prevent all weapons from being fired");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotBeFiredModifier(self, Filters.weapon),
                                            "Prevents all weapons from being fired"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    protected PlayoutDecisionEffect getEitherWayYouWinPlayoutDecisionEffect(final SwccgGame game, PhysicalCard self, PlayInterruptAction action, String somePlayer) {
        return new PlayoutDecisionEffect(action, somePlayer,
                new MultipleChoiceAwaitingDecision("Choose an option", new String[]{"Play a Defensive Shield from under Starting Effect", "Exchange a card in hand with one of your race destinies", "Do nothing"}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        if (index == 0) {

                            game.getGameState().sendMessage(somePlayer + " chooses to play a Defensive Shield from under their Starting Effect");
                            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(somePlayer), Filters.Starting_Effect));
                            if (startingEffect != null) {
                                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                                    final SubAction subAction = new SubAction(action, somePlayer);
                                    subAction.appendTargeting(
                                            new ChooseStackedCardEffect(action, somePlayer, startingEffect, filter) {
                                                @Override
                                                protected void cardSelected(PhysicalCard selectedCard) {
                                                    // Perform result(s)
                                                    subAction.appendEffect(
                                                            new PlayStackedDefensiveShieldEffect(action, startingEffect, selectedCard));
                                                }
                                            }
                                    );
                                    action.appendEffect(new StackActionEffect(action, subAction));
                                }
                            }
                        } else if (index == 1) {
                            game.getGameState().sendMessage(somePlayer + " chooses to exchange a card in hand with one of their race destinies");
                            if (GameConditions.hasHand(game, somePlayer)
                                    && GameConditions.hasRaceDestiny(game, somePlayer)){

                                    final SubAction subAction = new SubAction(action, somePlayer);
                                    subAction.appendEffect(
                                            new ExchangeCardFromHandWithStackedCardEffect(action, somePlayer, Filters.any, Filters.any, Filters.and(Filters.your(somePlayer), Filters.raceDestiny), true));
                                    action.appendEffect(new StackActionEffect(action, subAction));
                            }
                        } else {
                            game.getGameState().sendMessage(somePlayer + " chooses not to play a Defensive Shield or exchange a card with a race destiny");
                        }
                    }
                }
        );                            
    }
}
