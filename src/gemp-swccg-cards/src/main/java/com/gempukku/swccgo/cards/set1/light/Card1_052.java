package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Kessel Run
 */
public class Card1_052 extends AbstractUtinniEffect {
    public Card1_052() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Kessel_Run, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Planet Kessel has infamous glitterstim spice mines attracting smugglers and pirates. A 'Kessel run' is a long, dangerous hyper-route they must travel quickly.");
        setGameText("Deploy on Kessel. Target one of your smugglers at another system. X=parsec distance between the two systems. When target reaches Kessel, opponent draws destiny. If = 0, starship lost. Otherwise, by returning to first system, 'retrieve' X Lost Force.");
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Kessel_system;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.or(Filters.smuggler, Filters.mayMakeKesselRunInsteadOfSmuggler),
                Filters.at(Filters.and(Filters.system, Filters.not(Filters.Kessel_system))));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        final PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard otherSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.sameSystem(target));
            if (otherSystem != null) {
                final int distance = Math.abs(otherSystem.getParsec() - self.getAttachedTo().getParsec());
                Evaluator evaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
                    @Override
                    protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        return distance;
                    }
                };
                self.setWhileInPlayData(new WhileInPlayData(otherSystem, evaluator));
            }
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (!GameConditions.isUtinniEffectReached(game, self)) {
                if (GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
                    final PhysicalCard starship = Filters.findFirstActive(game, self, Filters.and(Filters.starship, Filters.hasAboard(target)));
                    if (starship != null) {

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setSingletonTrigger(true);
                        action.setText("Draw destiny");
                        // Update usage limit(s)
                        action.appendUsage(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                                    }
                                }
                        );
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, opponent) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        final GameState gameState = game.getGameState();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: No result due to failed destiny draw");
                                            return;
                                        }

                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                        if (totalDestiny == 0) {
                                            gameState.sendMessage("Result: Starship lost");
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, starship));
                                        } else {
                                            gameState.sendMessage("Result: No result");
                                        }
                                    }
                                });
                        return Collections.singletonList(action);
                    }
                }
            }
            else if (!GameConditions.isUtinniEffectCompleted(game, self)) {
                WhileInPlayData data = self.getWhileInPlayData();
                if (data != null) {
                    PhysicalCard otherSystem = data.getPhysicalCard();
                    if (GameConditions.canSpot(game, self, Filters.and(target, Filters.at(Filters.and(Filters.system, Filters.sameTitle(otherSystem)))))) {
                        float forceToRetrieve = 0;
                        Evaluator evaluator = data.getEvaluator();
                        if (evaluator != null) {
                            forceToRetrieve = evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self);
                        }

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setSingletonTrigger(true);
                        action.setText("Retrieve " + GuiUtils.formatAsString(forceToRetrieve) + " Force");
                        // Update usage limit(s)
                        action.appendUsage(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
                                    }
                                }
                        );
                        // Perform result(s)
                        action.appendEffect(
                                new RecordUtinniEffectCompletedEffect(action, self));
                        action.appendEffect(
                                new RetrieveForceEffect(action, playerId, forceToRetrieve) {
                                    @Override
                                    public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                        return Collections.singletonList(target);
                                    }
                                });
                        action.appendEffect(
                                new LoseCardFromTableEffect(action, self));
                        return Collections.singletonList(action);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data != null) {
            PhysicalCard otherSystem = data.getPhysicalCard();
            Evaluator evaluator = data.getEvaluator();
            return "System: " + GameUtils.getCardLink(otherSystem) + ", X = " + GuiUtils.formatAsString(evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self));
        }
        return null;
    }
}