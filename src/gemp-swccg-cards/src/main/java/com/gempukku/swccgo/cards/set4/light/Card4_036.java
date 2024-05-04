package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Subtype: Utinni
 * Title: Rycar's Run
 */
public class Card4_036 extends AbstractUtinniEffect {
    public Card4_036() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Rycars_Run, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("The infamous smuggler Rycar Ryjerd does this all the time. He's an idiot.");
        setGameText("Deploy on a Big One. X = twice the number of asteroid sectors at that system. Target a starfighter at related planet system. When reached by target, relocate Utinni Effect to planet system. When target returns to system, lose Utinni Effect. Retrieve X Force.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Big_One;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.starfighter, Filters.at(Filters.and(Filters.planet_system, Filters.relatedSystem(deployTarget))));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.starship;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final int numAsteroidSectors = Filters.countTopLocationsOnTable(game,
                    Filters.and(Filters.asteroid_sector, Filters.sameOrRelatedLocation(self)));
            Evaluator evaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
                @Override
                protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                    return 2 * numAsteroidSectors;
                }
            };
            self.setWhileInPlayData(new WhileInPlayData(evaluator));
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
            if (!GameConditions.isUtinniEffectReached(game, self)) {
                PhysicalCard planetSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.planet_system, Filters.relatedSystem(self)));
                if (planetSystem != null) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Relocate to " + GameUtils.getFullName(planetSystem));
                    action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(planetSystem));
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
                            new AttachCardFromTableEffect(action, self, planetSystem));
                    return Collections.singletonList(action);
                }
            }
            else if (!GameConditions.isUtinniEffectCompleted(game, self)) {
                float forceToRetrieve = 0;
                Evaluator evaluator = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getEvaluator() : null;
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
                        new LoseCardFromTableEffect(action, self));
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, forceToRetrieve));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        Evaluator evaluator = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getEvaluator() : null;
        if (evaluator != null) {
            return "X = " + GuiUtils.formatAsString(evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self));
        }
        return null;
    }
}