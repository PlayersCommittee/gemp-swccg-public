package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
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
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseOpponentsForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Beggar
 */
public class Card1_044 extends AbstractNormalEffect {
    public Card1_044() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Beggar, Uniqueness.UNIQUE);
        setLore("Many Mos Eisley citizens, once swindled and robbed, become destitute. Unable to afford off-planet passage, they live in the streets and do odd jobs or beg.");
        setGameText("Use 3 Force to deploy on any exterior Tatooine site (free at Beggar's Canyon). You may use any amount of Force in the opponent's Force Pile during your turns. However, if you use more than 1 of the opponent's Force in a turn, Beggar is lost.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Beggars_Canyon));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_Tatooine_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseOpponentsForceModifier(self, new PlayersTurnCondition(playerId), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId))  {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), 0);
            if (maxForceToUseViaCard > 0) {
                final int maxForceToUse = Math.min(maxForceToUseViaCard, useForceEffect.getTotalAmountOfForceToUse());
                if (maxForceToUse > 0) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setRepeatableTrigger(true);
                    action.setText("Use opponent's Force");
                    // Perform result(s)
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose amount of opponent's Force to use", 1, maxForceToUse, maxForceToUse) {
                                        @Override
                                        public void decisionMade(int result) throws DecisionResultInvalidException {
                                            final int validatedResult = Math.min(maxForceToUse, result);
                                            useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + validatedResult);
                                            Float forceUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : null;
                                            if (forceUsed == null)
                                                forceUsed = (float) validatedResult;
                                            else
                                                forceUsed += validatedResult;
                                            self.setWhileInPlayData(new WhileInPlayData(forceUsed));
                                        }
                                    }
                            )
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId)) {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            int minOpponentForceToUse = Math.max(0, useForceEffect.getTotalAmountOfForceToUse() - game.getGameState().getForcePile(playerId).size());
            if (minOpponentForceToUse > 0) {
                final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), minOpponentForceToUse);
                if (maxForceToUseViaCard > 0) {
                    final int maxForceToUse = Math.min(maxForceToUseViaCard, useForceEffect.getTotalAmountOfForceToUse());

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setRepeatableTrigger(true);
                    action.setText("Use opponent's Force");
                    // Perform result(s)
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose amount of opponent's Force to use", 1, maxForceToUse, maxForceToUse) {
                                        @Override
                                        public void decisionMade(int result) throws DecisionResultInvalidException {
                                            final int validatedResult = Math.min(maxForceToUse, result);
                                            useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + validatedResult);
                                            Float forceUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : null;
                                            if (forceUsed == null)
                                                forceUsed = (float) validatedResult;
                                            else
                                                forceUsed += validatedResult;
                                            self.setWhileInPlayData(new WhileInPlayData(forceUsed));
                                        }
                                    }
                            )
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            Float forceUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : null;
            if (forceUsed != null && forceUsed > 1) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        // Check condition(s)
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }
}