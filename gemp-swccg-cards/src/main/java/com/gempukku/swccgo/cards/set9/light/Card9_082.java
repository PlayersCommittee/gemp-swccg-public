package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron 4
 */
public class Card9_082 extends AbstractStarfighter {
    public Card9_082() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, "Red Squadron 4", Uniqueness.UNIQUE);
        setLore("Flown by Derek 'Hobbie' Klivian during the attack on the second Death Star. Second-highest mission total of any X-wing in service for the Alliance.");
        setGameText("May add 1 pilot. Hobbie deploys -2 aboard. When firing X-wing Laser Cannons, may use 2 Force to make X = 3. When Hobbie piloting, maneuver +2 and immune to attrition < 4.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Hobbie);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Hobbie, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Hobbie, -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition hobbiePiloting = new HasPilotingCondition(self, Filters.Hobbie);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, hobbiePiloting, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hobbiePiloting, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.X_wing_Laser_Cannon, self)
                && GameConditions.canUseForce(game, playerId, 2)) {
            PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();
            if (weapon != null) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make X = 3");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                // Perform result(s)
                action.appendEffect(
                        new AddUntilEndOfWeaponFiringModifierEffect(action, new ResetCalculationVariableModifier(self, weapon, 3, Variable.X), "Makes X = 3"));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
