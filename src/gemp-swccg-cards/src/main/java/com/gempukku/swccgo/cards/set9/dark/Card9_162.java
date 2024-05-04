package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetCalculationVariableModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Saber 1
 */
public class Card9_162 extends AbstractStarfighter {
    public Card9_162() {
        super(Side.DARK, 3, 2, 3, null, 4, null, 4, Title.Saber_1, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("TIE interceptor serial number 000004. Assigned to Baron Soontir Fel of the fighting 181st. Bears the Saber Squadron bloodstripe, representing a minimum of 10 kills.");
        setGameText("May add 1 pilot. Fel deploys -1 aboard. When firing SFS L-s9.3 Laser Cannons, may use 1 Force to make X = 3. Immune to attrition < 5 when Fel piloting.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.SABER_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Fel);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Fel, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Fel, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition felPiloting = new HasPilotingCondition(self, Filters.Fel);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, felPiloting, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.SFS_Lx93_Laser_Cannons, self)
                && GameConditions.canUseForce(game, playerId, 1)) {
            PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();
            if (weapon != null) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make X = 3");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Perform result(s)
                action.appendEffect(
                        new AddUntilEndOfWeaponFiringModifierEffect(action, new ResetCalculationVariableModifier(self, weapon, 3, Variable.X), "Makes X = 3"));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
