package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Objective
 * Title: He Is The Chosen One / He Will Bring Balance
 */
public class Card208_025_BACK extends AbstractObjective {
    public Card208_025_BACK() {
        super(Side.LIGHT, 7, Title.He_Will_Bring_Balance, ExpansionSet.SET_8, Rarity.V);
        setGameText("While this side up, once during your control phase, may peek at up to X cards from the top of your Reserve Deck, where X = number of battlegrounds you occupy; take one into hand and shuffle your Reserve Deck. During your draw phase, may retrieve any one card; opponent may stack a card from hand face down on I Feel The Conflict to place that card out of play instead. Flip this card (unless Vader crossed over) if opponent's character of ability > 4 at a battleground site or you do not have Luke (or a Jedi) at a battleground site.");
        addIcons(Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final int maxValueOfX = (int) Math.min(game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X,
                    Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId)))),
                    game.getGameState().getReserveDeckSize(playerId));
            if (maxValueOfX > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at top cards of Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose number of cards to peek at", 1, maxValueOfX, maxValueOfX) {
                                    @Override
                                    public void decisionMade(final int numToDraw) {
                                        action.appendEffect(
                                                new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, maxValueOfX, 1, 1));
                                        action.appendEffect(
                                                new ShuffleReserveDeckEffect(action));
                                    }
                                }
                        ));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a card");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.any) {
                        @Override
                        protected void cardRetrieved(final PhysicalCard retrievedCard) {
                            final GameState gameState = game.getGameState();
                            final String opponent = game.getOpponent(playerId);
                            if (GameConditions.hasHand(game, opponent)
                                    && GameConditions.canBePlacedOutOfPlay(game, retrievedCard)) {
                                final PhysicalCard iFeelTheConflict = Filters.findFirstActive(game, self, Filters.I_Feel_The_Conflict);
                                if (iFeelTheConflict != null) {
                                    // Ask player to Use Force or retrieval is canceled
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, opponent,
                                                    new YesNoDecision("Do you want to stack a card from hand on " + GameUtils.getCardLink(iFeelTheConflict) + " to place " + GameUtils.getCardLink(retrievedCard) + " out of play") {
                                                        @Override
                                                        protected void yes() {
                                                            SubAction subAction = new SubAction(action, opponent);
                                                            if (GameConditions.canTarget(game, self, Filters.title("His Destiny"))) {
                                                                subAction.appendCost(
                                                                        new StackCardFromHandEffect(subAction, opponent, iFeelTheConflict, Filters.any, true, false, false, false));
                                                            } else {
                                                                subAction.appendCost(
                                                                        new StackCardFromHandEffect(subAction, opponent, iFeelTheConflict));
                                                            }
                                                            subAction.appendEffect(
                                                                    new PlaceCardOutOfPlayFromOffTableEffect(subAction, retrievedCard));
                                                            action.appendEffect(
                                                                    new StackActionEffect(action, subAction));
                                                        }

                                                        @Override
                                                        protected void no() {
                                                            gameState.sendMessage(opponent + " chooses to not stack a card from hand on " + GameUtils.getCardLink(iFeelTheConflict));
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.wonBattle(game, effectResult, Filters.Luke)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Re-circulate and reshuffle");
            action.setActionMsg("Re-circulate and shuffle Reserve Deck");

            action.appendEffect(
                    new RecirculateEffect(action, playerId));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.isCrossedOver(game, Persona.VADER)
                && (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(4), Filters.at(Filters.battleground_site)))
                || !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.or(Filters.Luke, Filters.Jedi), Filters.at(Filters.battleground_site))))) {

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