package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Eject! Eject!
 */
public class Card2_032 extends AbstractNormalEffect {
    public Card2_032() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Eject! Eject!");
        setLore("The Guiderhouser ejection seat has saved many pilots from their exploding starships. However, system malfunctions have sometimes caused spontaneous ejection.");
        setGameText("Use 1 Force to target a starfighter having one or more permanent pilots. Draw destiny. If destiny > 2, deploy on starfighter to remove all permanent pilots (otherwise, Effect is lost). May add 1 pilot for each permanent pilot removed. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.starfighter, Filters.hasPermanentPilot);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final String playerId = self.getOwner();
            final PhysicalCard starship = self.getAttachedTo();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 2) {
                                gameState.sendMessage("Result: Succeeded");
                                final int numPermanentPilots = game.getModifiersQuerying().getPermanentPilotsAboard(game.getGameState(), starship).size();
                                self.setWhileInPlayData(new WhileInPlayData((float) numPermanentPilots));
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition inPlayDataSet = new InPlayDataSetCondition(self);
        Filter starship = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelIconModifier(self, starship, inPlayDataSet, Icon.PILOT));
        modifiers.add(new RemovePermanentPilotsModifier(self, inPlayDataSet, starship));
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new PilotCapacityModifier(self, starship, inPlayDataSet, new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);
                return self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : 0;
            }
        }));
        return modifiers;
    }
}