package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
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
        setGameText("May add 1 pilot. May move as a 'react'. Matching vehicle for any Resistance pilot. Pilot's power = 0, and when targeted by an Interrupt or weapon, may use this vehicle's defense value instead.");
        addModelType(ModelType.V_4X);
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

        // Bo
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
                            "Resets pilot's defense value to " + GuiUtils.formatAsString(defenseValue)));
            actions.add(action);
        }


        // Handle an Interrupt-targeting.
        // Note that we need to handle 2 cases here:
        //   1. Targeted Defense Value (we can just increase defense value to 5)
        //   2. Targeted Ability - we CANT bump ability to 5. We just need to use
        //      ability of 5 for the purposes of that interrupt (and no others).
        //      We don't want to flip objectives, have weird interactions with Tatooine Maul, etc

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Interrupt, pilotOfself)) {

            PhysicalCard interruptCard = ((RespondablePlayingCardEffect) effect).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Have character use vehicle's defense value");

            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), self);
            action.appendEffect(
                    new AddUntilEndOfCardPlayedModifierEffect(action, interruptCard,
                            new ResetDefenseValueModifier(self, pilotOfself, defenseValue),
                            "Resets pilot's defense value to " + GuiUtils.formatAsString(defenseValue)));
            actions.add(action);
        }

        return actions;
    }
}
