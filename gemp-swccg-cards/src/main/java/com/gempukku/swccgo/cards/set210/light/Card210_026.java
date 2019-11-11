package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.UseSpecificAbilityVsCardModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.*;


/**
 * Set: Set 10
 * Type: Vehicle
 * Subtype: Combat
 * Title: V-4X-D Speeder
 */
public class Card210_026 extends AbstractCombatVehicle {
    public Card210_026() {
        super(Side.LIGHT, 5, 2, 2, null, 5, 3, 4, "V-4X-D Ski Speeder");
        setLore("");
        setGameText("May add 1 Pilot. May move as a 'react'. Matching vehicle for any resistance pilot. Pilot's power = 0, and if targeted by Force Lightning, Trample, or a weapon, may use this card's defense value instead.");
        addModelType(ModelType.V_4X);
        addKeyword(Keyword.SPEEDER);
        addIcons(Icon.EPISODE_VII);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.and(Filters.pilot, Filters.Resistance_character));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ResetPowerModifier(self, Filters.piloting(self), 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CharactersAboardMayJumpOffModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {

        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Character piloting the Ski Speeder
        Filter pilotOfself = Filters.piloting(self);

        // Handle the Weapon-targeting:

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, pilotOfself, Filters.any) &&
                GameConditions.canSpot(game, self, pilotOfself)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Have character use vehicle's defense value");

            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), self);
            action.appendEffect(
                    new AddUntilEndOfWeaponFiringModifierEffect(action,
                            new ResetDefenseValueModifier(self, pilotOfself, defenseValue),
                            "Use vehicle's defense value (" + GuiUtils.formatAsString(defenseValue) + ") vs weapon."));
            actions.add(action);
        }


        // Allow responding to Trample

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Trample, pilotOfself)) {

            PhysicalCard interruptCard = ((RespondablePlayingCardEffect) effect).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Have character use vehicle's defense value");

            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), self);

            // For Trample, set the "vs specific card" modifier
            action.appendEffect(
                    new AddUntilEndOfCardPlayedModifierEffect(action, interruptCard,
                            new UseSpecificAbilityVsCardModifier(self, pilotOfself, defenseValue, interruptCard),
                            "Use vehicle's defense value (" +  GuiUtils.formatAsString(defenseValue) + ") instead of ability vs Trample."));
            actions.add(action);
        }


        // Allow responding to Force Lightning

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Force_Lightning, pilotOfself)) {

            PhysicalCard interruptCard = ((RespondablePlayingCardEffect) effect).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Have character use vehicle's defense value");

            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), self);

            // Temporarily rest the defense value
            action.appendEffect(
                    new AddUntilEndOfCardPlayedModifierEffect(action, interruptCard,
                            new ResetDefenseValueModifier(self, pilotOfself, defenseValue),
                            "Use vehicle's defense value (" + GuiUtils.formatAsString(defenseValue) + ") vs Force Lightning"));

            actions.add(action);
        }

        return actions;
    }
}
