package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Starship
 * Subtype: Starfighter
 * Title: Din Djarin's Modified N-1
 */
public class Card225_043 extends AbstractStarfighter {
    public Card225_043() {
        super(Side.LIGHT, 3, 3, 3, null, 5, 3, 5, "Din Djarin's Modified N-1", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("");
        setGameText("May add 1 pilot and Grogu as a passenger. " +
                "Din Djarin deploys -1 aboard. While Din piloting, immune to attrition < 5 " +
                "and once per game may use 3 Force to cancel a battle just initiated here.");
        addIcons(Icon.NAV_COMPUTER, Icon.INDEPENDENT, Icon.VIRTUAL_SET_24);
        setPilotCapacity(1);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.Din);
    }

    @Override
    protected Filter getGameTextValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Grogu;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Din, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Din, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Din), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DIN_DJARINS_MODIFIED_N_1__CANCEL_BATTLE;
        if(TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.hasPiloting(game, self, Filters.Din)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)){
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use 3 force to cancel battle");
            action.setActionMsg("Use 3 force to cancel battle");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendCost(
                    new CancelBattleEffect(action)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}