package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Maneuvering Flaps
 */
public class Card7_069 extends AbstractNormalEffect {
    public Card7_069() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Maneuvering_Flaps);
        setLore("Enhanced steering mechanisms on Rebel T-47s provide increased maneuverability in planetary atmospheres.");
        setGameText("Deploy on your side of table. Once during each of your control phases, may use 1 Force: your combat vehicles and shuttle vehicles are power and forfeit +2, maneuver +1 and landspeed=0 until start of your next turn. (Immune to Alter if Luke or Zev on table.)");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Engage Maneuvering Flaps");
            action.setActionMsg("Make " + playerId + "'s combat vehicles and shuttle vehicles power +2, forfeit +2, maneuver +1, and landspeed = 0");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            action.appendEffect(
                    new SendMessageEffect(action, "Maneuvering Flaps engaged"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCombatVehiclesAndShuttleVehicles = Filters.and(Filters.your(self), Filters.or(Filters.combat_vehicle, Filters.shuttle_vehicle));
        Condition condition = new InPlayDataSetCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourCombatVehiclesAndShuttleVehicles, condition, 2));
        modifiers.add(new ForfeitModifier(self, yourCombatVehiclesAndShuttleVehicles, condition, 2));
        modifiers.add(new ManeuverModifier(self, yourCombatVehiclesAndShuttleVehicles, condition, 1));
        modifiers.add(new ResetLandspeedModifier(self, yourCombatVehiclesAndShuttleVehicles, condition, 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OnTableCondition(self, Filters.or(Filters.Luke, Filters.Zev)), Title.Alter));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Disengage Maneuvering Flaps");
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, null));
            action.appendEffect(
                    new SendMessageEffect(action, "Maneuvering Flaps disengaged"));
            return Collections.singletonList(action);
        }
        return null;
    }
}