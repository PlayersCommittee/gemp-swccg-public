package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Utinni
 * Title: The First Transport Is Away!
 */
public class Card3_039 extends AbstractUtinniEffect {
    public Card3_039() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.The_First_Transport_Is_Away, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("'When you've gotten past the energy shield, proceed directly to the rendezvous point. Understood? Good luck!'");
        setGameText("Deploy on any system (except Hoth). Target a Medium Transport at a Hoth site. When reached by target: Retrieve X Force, where X = twice the number of passengers. Relocate Utinni Effect to Hoth system. Your total power is +2 in battles at Hoth sites.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.system, Filters.except(Filters.Hoth_system));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.medium_transport, Filters.at(Filters.Hoth_site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Hoth_site, Filters.battleLocation),
                new AttachedCondition(self, Filters.Hoth_system), 2, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        final int permCardId = self.getPermanentCardId();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            Evaluator evaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
                @Override
                protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                    PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                    if (self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1) != null) {
                        return 2 * Filters.countActive(game, self, Filters.and(Filters.aboardAsPassenger(self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1))));
                    }
                    return 0;
                }
            };
            self.setWhileInPlayData(new WhileInPlayData(evaluator));
        }

        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
            float forceToRetrieve = 0;
            Evaluator evaluator = self.getWhileInPlayData().getEvaluator();
            if (evaluator != null) {
                forceToRetrieve = evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self);
            }
            PhysicalCard hothSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.Hoth_system);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Retrieve " + GuiUtils.formatAsString(forceToRetrieve) + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard self = gameState.findCardByPermanentId(permCardId);
                            self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new RecordUtinniEffectCompletedEffect(action, self));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, forceToRetrieve));
            if (hothSystem != null) {
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, hothSystem));
            }
            return Collections.singletonList(action);
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