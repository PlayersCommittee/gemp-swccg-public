package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Starship
 * Subtype: Starfighter
 * Title: Blue Squadron 1
 */

public class Card210_005 extends AbstractStarfighter {
    public Card210_005() {
        super(Side.LIGHT, 2, 3, 3, null, 5, 5, 5, "Blue Squadron 1", Uniqueness.UNIQUE);
        setLore("");
        setLore("May add 1 pilot. Snap deploys free aboard. While Snap piloting, immune to attrition < 5 and, once per turn, may use 1 Force to cancel a Force drain at another system within 1 parsec of Snap.");
        addPersonas(Persona.BLUE_SQUADRON_1);
        addIcons(Icon.RESISTANCE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.BLUE_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Snap);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployForFreeForSimultaneouslyDeployingPilotModifier(self, Filters.Snap));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.Snap, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, self, new HasPilotingCondition(self, Filters.Snap), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter systemsWithinOneParsec = Filters.withinParsecsOf(self, 1);
        Filter notSystemPresent = Filters.not(Filters.sameSystem(self));
        Filter validSystems = Filters.and(systemsWithinOneParsec, notSystemPresent);

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, validSystems)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasPiloting(game, self, Filters.Snap))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force Drain");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}