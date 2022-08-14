package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Objective
 * Title: No Money, No Parts, No Deal! / You're A Slave?
 */
public class Card12_180_BACK extends AbstractObjective {
    public Card12_180_BACK() {
        super(Side.DARK, 7, Title.Youre_A_Slave);
        setGameText("While this side up, once during each of your deploy phases, may place a card from hand face down on your side of the table and opponent must choose to lose 2 Force (you place card in Used Pile) or use 2 Force (you deploy that card for free). If opponent uses 2 Force and you cannot deploy card, lose 2 Force and card is lost. Opponent's non-unique aliens are each deploy +3. Flip this card if Watto not present at Watto's Junkyard or you do not occupy Mos Espa.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final String opponent = game.getOpponent(playerId);

        Filter filter = Filters.and(Filters.Watto, Filters.at(Filters.Wattos_Junkyard));

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 8)
                && GameConditions.canTarget(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Watto in Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Watto to place in Used Pile", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 8));
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, targetedCard));
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, opponent,
                                                            new IntegerAwaitingDecision("Choose amount of Force to retrieve", 0, 4, 4) {
                                                                @Override
                                                                public void decisionMade(final int amountToRetrieve) throws DecisionResultInvalidException {
                                                                    GameState gameState = game.getGameState();
                                                                    if (amountToRetrieve == 0) {
                                                                        gameState.sendMessage(opponent + " chooses to not retrieve any Force");
                                                                        return;
                                                                    }
                                                                    gameState.sendMessage(opponent + " chooses to retrieve " + amountToRetrieve + " Force");
                                                                    action.appendEffect(
                                                                            new RetrieveForceEffect(action, opponent, amountToRetrieve));
                                                                }
                                                            }
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.opponents(self), Filters.non_unique, Filters.alien), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card face down on side of table");
            action.setActionMsg("Place a card face down on side of table");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardFromHandFaceDownOnSideOfTableEffect(action, playerId) {
                        @Override
                        protected void cardPlacedFaceDownOnSideOfTable(final PhysicalCard card) {
                            final GameState gameState = game.getGameState();
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose 2 Force (place card in Used Pile)", "Use 2 Force (opponent deploys card for free)"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            opponentLosesTwoForceAndPlaceCardInUsedPile(action, game, self, gameState, playerId, opponent, card, opponent + " chooses to lose 2 Force and place card in Used Pile");
                                                        } else {
                                                            gameState.sendMessage(opponent + " chooses to use 2 Force to allow opponent to deploy card for free");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 2));
                                                            // Flip over card and deploy if possible
                                                            gameState.sendMessage(playerId + "'s card on side of table is " + GameUtils.getCardLink(card));
                                                            gameState.cardAffectsCard(playerId, card, card);

                                                            // Check if player can deploy the card for free
                                                            boolean deployable = Filters.and(Filters.deployable(self, null, true, 0)).accepts(game, card);

                                                            if (deployable) {
                                                                // Deploy card for free
                                                                action.appendEffect(
                                                                        new StackActionEffect(action, card.getBlueprint().getPlayCardAction(card.getOwner(), game, card,
                                                                                self, true, 0, null, null, null, null, null, false, 0, Filters.any, null)));
                                                            } else {
                                                                gameState.sendMessage(playerId + " is not able to deploy " + GameUtils.getCardLink(card));
                                                                // Lose 2 Force and card
                                                                action.appendEffect(
                                                                        new LoseForceEffect(action, playerId, 2));
                                                                action.appendEffect(
                                                                        new LoseCardsFromOffTableSimultaneouslyEffect(action, Collections.singleton(card), false));
                                                            }
                                                        }
                                                    }
                                                }
                                        )
                                );
                            } else {
                                opponentLosesTwoForceAndPlaceCardInUsedPile(action, game, self, gameState, playerId, opponent, card, "Only option available is for " + opponent + " to lose 2 Force and place card in Used Pile");
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    private void opponentLosesTwoForceAndPlaceCardInUsedPile(final Action action, SwccgGame game, PhysicalCard self, GameState gameState, final String playerId, String opponent, PhysicalCard card, String message) {
        gameState.sendMessage(message);
        action.appendEffect(
                new LoseForceEffect(action, opponent, 2));
        action.appendEffect(
                new PutCardFromFaceDownOnSideOfTableInUsedPileEffect(action, playerId, card));
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.YOURE_A_SLAVE__DRAW_TOP_CARD_OF_RESERVE_DECK_WHEN_PLACING_A_CARD_IN_USED_PILE)
                && GameConditions.hasReserveDeck(game, playerId)) {
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Draw a card From Reserve Deck?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                                }

                            }));
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (!GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Watto, Filters.presentAt(Filters.Wattos_Junkyard)))
                || !GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Mos_Espa))) {

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