package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceBattleDamageEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: The Professor
 */
public class Card4_040 extends AbstractNormalEffect {
    public Card4_040() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Professor, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Protocol droids are programmed to interface with a variety of computer technologies. Quick and precise interpretation can dramatically increase operational efficiency.");
        setGameText("Use 3 Force to deploy on your side of the table (free if C-3PO on table). If you have a protocol droid aboard a starship or vehicle in a battle, you may use X Force to reduce the battle damage against you by X (by X + 1 if an astromech character is also aboard).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeModifier(self, new OnTableCondition(self, Filters.C3PO)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isBattleDamageRemaining(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle), Filters.participatingInBattle,
                Filters.hasAboard(self, Filters.and(Filters.your(playerId), Filters.protocol_droid))))) {
            int maxForceToUse = GameConditions.forceAvailableToUse(game, playerId);
            if (maxForceToUse > 0) {
                final BattleState battleState = game.getGameState().getBattleState();
                float damageRemaining = battleState.getBattleDamageRemaining(game, playerId);
                final boolean astromechAboard = GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle), Filters.participatingInBattle,
                        Filters.hasAboard(self, Filters.and(Filters.your(playerId), Filters.protocol_droid)),
                        Filters.hasAboard(self, Filters.and(Filters.your(playerId), Filters.astromech_droid, Filters.character))));
                int defaultForceToUse = Math.min(Math.max((int) Math.ceil(damageRemaining) - (astromechAboard ? 1 : 0), 1), maxForceToUse);

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce battle damage");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, defaultForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        int reduceBy = astromechAboard ? result + 1 : result;
                                        action.setActionMsg("Reduce battle damage by " + reduceBy);
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ReduceBattleDamageEffect(action, playerId, reduceBy));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}