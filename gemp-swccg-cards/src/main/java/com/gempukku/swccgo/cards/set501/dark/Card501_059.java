package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.LandedCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Starfighter
 * Title: First Light
 */
public class Card501_059 extends AbstractStarfighter {
    public Card501_059() {
        super(Side.DARK, 3, 3, 3, null, 3, 3, 6, "First Light", Uniqueness.UNIQUE);
        setGameText("May add 3 pilots and 3 passengers. May deploy to exterior sites. If it just took off, may use its hyperspeed and land. Immune to attrition < 3 (< 6 if Vos aboard, even while landed).");
        addIcons(Icon.SCOMP_LINK, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_13);
        addModelType(ModelType.KALEVALAN_YACHT);
        addPersona(Persona.FIRST_LIGHT);
        setPilotCapacity(3);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Vos);
        setTestingText("First Light");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, Filters.exterior_site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition hasVosAboardCondition = new HasAboardCondition(self, Filters.Vos);
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new AndCondition(hasVosAboardCondition, new LandedCondition(self)), 6));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition hasVosAboardCondition = new HasAboardCondition(self, Filters.Vos);
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 6, hasVosAboardCondition)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter eligibleSystem = Filters.and(Filters.anotherLocation(self), Filters.relatedSystemTo(self, Filters.exterior_site));
        if (TriggerConditions.justTookOff(game, effectResult, self)
                && Filters.movableAsAdditionalMove(playerId).accepts(game, self)
                && GameConditions.canSpot(game, self, eligibleSystem)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Move using hyperspeed and land");
            action.setActionMsg("Move using hyperspeed and land");
            action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, eligibleSystem));
            action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, Filters.exterior_site));
            return Collections.singletonList(action);
        }
        return null;
    }
}
