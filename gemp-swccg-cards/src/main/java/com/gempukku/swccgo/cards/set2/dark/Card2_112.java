package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HypoQuestionAnsweredResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Hypo
 */
public class Card2_112 extends AbstractCharacterDevice {
    public Card2_112() {
        super(Side.DARK, 5, Title.Hypo);
        setLore("Truth drugs like Bavo Six decrease resistance to interrogation. The enormous needle adds psychological pressure, facilitating the interrogation process.");
        setGameText("Deploy on your IT-O. Once during each of your control phases, if present with a captive: You may ask one yes-or-no question about cards in opponent's hand. Opponent must answer truthfully or lose 1 Force. OR May add 1 to Force drain where present.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.IT0);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.IT0;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.or(Filters.and(Filters.captive, Filters.presentWith(self)),
                Filters.and(Filters.imprisoned, Filters.with(self))), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_TORTURED)))
                && GameConditions.hasHand(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Ask yes-or-no question");
            action.setActionMsg("Ask a yes-or-no question about cards in opponent's hand in the chat window");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Select OK after typing yes-or-no question in chat window", new String[]{"OK"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    final GameState gameState = game.getGameState();
                                    gameState.sendMessage("Awaiting answer from " + opponent);
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, opponent,
                                                    new MultipleChoiceAwaitingDecision("Choose answer to yes-or-no question (typed in the chat window)", new String[]{"Yes", "No", "Refuse to answer (Lose 1 Force)"}) {
                                                        @Override
                                                        protected void validDecisionMade(int index, String result) {
                                                            if (index == 0) {
                                                                gameState.sendMessage(opponent + " answers 'Yes'");
                                                                action.appendEffect(
                                                                        new TriggeringResultEffect(action,
                                                                                new HypoQuestionAnsweredResult(opponent, HypoQuestionAnsweredResult.Answer.YES)));
                                                            }
                                                            else if (index == 1) {
                                                                gameState.sendMessage(opponent + " answers 'No'");
                                                                action.appendEffect(
                                                                        new TriggeringResultEffect(action,
                                                                                new HypoQuestionAnsweredResult(opponent, HypoQuestionAnsweredResult.Answer.NO)));
                                                            }
                                                            else {
                                                                gameState.sendMessage(opponent + " refused to answer the yes-or-no question");
                                                                action.appendEffect(
                                                                        new LoseForceEffect(action, opponent, 1));
                                                            }
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseDevice(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}