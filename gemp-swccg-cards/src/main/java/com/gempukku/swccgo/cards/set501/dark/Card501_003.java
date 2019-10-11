package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: Dark Jedi Master
 * Title: Darth Tyranus
 */
public class Card501_003 extends AbstractDarkJediMaster {
    public Card501_003() {
        super(Side.DARK, 1, 6, 5, 7, 8, "Darth Tyranus", Uniqueness.UNIQUE);
        setLore("Serennian leader. Trade Federation.");
        setGameText("Defense value and power +1 while armed with a lightsaber. Force loss from Force drains here cannot be reduced. During battle or lightsaber combat involving Dooku, may cancel and redraw opponent’s just drawn destiny. Immune to attrition < 6");
        addPersona(Persona.DOOKU);
        setSpecies(Species.SERENNIAN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.SEPARATIST, Icon.VIRTUAL_SET_12);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition armedWithLightsaber = new ArmedWithCondition(self, Filters.lightsaber);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, armedWithLightsaber, 1));
        modifiers.add(new DefenseValueModifier(self, armedWithLightsaber, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), game.getOpponent(self.getOwner()), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && (GameConditions.isInBattle(game, self) || TriggerConditions.isDestinyDrawType(game, effectResult, DestinyType.LIGHTSABER_COMBAT_DESTINY))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }
        return actions;
    }
}