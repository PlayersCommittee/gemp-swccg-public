package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityOfPilotEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Starfighter
 * Title: Plo Koon's Jedi Starfighter
 */
public class Card215_001 extends AbstractStarfighter {
    public Card215_001() {
        super(Side.LIGHT, 2, 2, 2, null, 0, 6, 5, "Plo Koon's Jedi Starfighter", Uniqueness.UNIQUE);
        setGameText("May add 1 Jedi pilot. *Maneuver = pilot's ability. While Plo piloting, power +2, immune to attrition < 3, and once per turn, may cancel and redraw your weapon or battle destiny just drawn here.");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_15);
        addModelType(ModelType.JEDI_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.persona(Persona.PLO));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        Condition ploPilotingCondition = new HasPilotingCondition(self, Persona.PLO);
        modifiers.add(new PowerModifier(self, ploPilotingCondition, 2));
        modifiers.add(new DefinedByGameTextManeuverModifier(self, new AbilityOfPilotEvaluator(self)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, ploPilotingCondition, 3));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Jedi;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId)
                || TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.hasPiloting(game, self, Persona.PLO)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
