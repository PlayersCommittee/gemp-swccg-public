package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.JediTestCompletedCondition;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Jedi Test
 * Title: It Is The Future You See
 */
public class Card4_078 extends AbstractJediTest {
    public Card4_078() {
        super(Side.LIGHT, 5, "It Is The Future You See");
        setGameText("Deploy on a Dagobah site. Target a mentor on Dagobah and an apprentice who has completed Jedi Test #4. Attempt when apprentice is present at the beginning of your control phase. Turn apprentice upside down (cannot move and power = 0). At the end of your next turn, turn apprentice right side up (restored): Place on apprentice. Immune to attrition < 4. Reveal the top two cards of your Reserve Deck and place one upside down on apprentice. Whenever you are about to draw a card for destiny, you may instead use the upside-down card (which remains on apprentice for re-use).");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.JEDI_TEST_5);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dagobah_site;
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.on(Title.Dagobah);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.Jedi_Test_4));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)) {
            if (!GameConditions.isJediTestBeingAttempted(game, self) && !GameConditions.isJediTestCompleted(game, self)) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
                if (apprentice != null) {
                    final int nextTurnNumber = gameState.getPlayersLatestTurnNumber(playerId) + 1;

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Attempt Jedi Test #5");
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    self.setJediTestStatus(JediTestStatus.ATTEMPTING);
                                    modifiersQuerying.attemptedJediTest(self, apprentice);
                                }
                            });
                    // Perform result(s)
                    action.appendEffect(
                            new RotateCardEffect(action, apprentice, true));
                    action.appendEffect(
                            new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                    new MayNotMoveModifier(self, Filters.and(apprentice, Filters.targetedByUncompletedJediTest(self))), null));
                    action.appendEffect(
                            new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                    new ResetPowerModifier(self, Filters.and(apprentice, Filters.targetedByUncompletedJediTest(self)), 0), null));
                    final int cardId = self.getCardId();
                    final int permCardId = self.getPermanentCardId();
                    action.appendEffect(
                            new AddUntilEndOfPlayersNextTurnActionProxyEffect(action,
                                    new AbstractActionProxy() {
                                        @Override
                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
                                            final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                            // Check condition(s)
                                            if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)
                                                    && self.getCardId() == cardId
                                                    && GameConditions.isJediTestBeingAttempted(game, self)
                                                    && GameConditions.isTurnNumber(game, nextTurnNumber)
                                                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, apprentice)) {

                                                RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action1.setText("Turn " + GameUtils.getFullName(apprentice) + " right side up");
                                                // Perform result(s)
                                                action1.appendEffect(
                                                        new RotateCardEffect(action1, apprentice, false));
                                                action1.appendEffect(
                                                        new CompleteJediTestEffect(action1, self));
                                                actions.add(action1);
                                            }
                                            return actions;
                                        }
                                    }, playerId));
                    actions.add(action);
                }
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)) {
            final GameState gameState = game.getGameState();
            if (GameConditions.isJediTestCompleted(game, self)) {
                final PhysicalCard stackedDestinyCard = Filters.findFirstFromStacked(game, Filters.and(Filters.stackedViaJediTest5,
                        Filters.stackedOn(self, Filters.or(self.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE), Filters.Jedi_Test_5))));
                if (stackedDestinyCard != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Substitute destiny");
                    // Pay cost(s)
                    action.appendCost(
                            new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(stackedDestinyCard)) {
                                @Override
                                protected void refreshedPrintedDestinyValues() {
                                    final float destinyNumber = game.getModifiersQuerying().getDestiny(game.getGameState(), stackedDestinyCard);
                                    action.setActionMsg("Substitute " + GameUtils.getCardLink(stackedDestinyCard) + "'s destiny value of " + GuiUtils.formatAsString(destinyNumber) + " for destiny");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new SubstituteDestinyEffect(action, destinyNumber));
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.apprenticeTargetedByJediTest(self), new JediTestCompletedCondition(self), 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.jediTestCompleted(game, effectResult, self)
                && GameConditions.hasReserveDeck(game, playerId)) {
            GameState gameState = game.getGameState();
            final PhysicalCard cardToStackOn = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5) ? self : self.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
            if (cardToStackOn != null && GameConditions.canSpot(game, self, cardToStackOn)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Reveal top two cards of Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new RevealTopCardsOfReserveDeckEffect(action, playerId, 2) {
                            @Override
                            protected void cardsRevealed(List<PhysicalCard> cards) {
                                action.appendEffect(
                                        new ChooseArbitraryCardsEffect(action, playerId, "Choose destiny card to stack", cards, 1, 1) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                PhysicalCard cardToStack = selectedCards.iterator().next();
                                                if (cardToStack != null) {
                                                    action.appendEffect(
                                                            new StackOneCardFromPileEffect(action, playerId, cardToStack, cardToStackOn, false, false, true));
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}