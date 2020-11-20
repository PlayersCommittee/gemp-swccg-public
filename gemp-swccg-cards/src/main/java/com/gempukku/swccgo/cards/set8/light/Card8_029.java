package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DeactivateTheShieldGeneratorTotalModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Sergeant Junkin
 */
public class Card8_029 extends AbstractRebel {
    public Card8_029() {
        super(Side.LIGHT, 2, 3, 3, 2, 4, "Sergeant Junkin", Uniqueness.UNIQUE);
        setLore("Demolitions expert and scout. Supervises explosives preparations for covert operations. Consulted with General Madine to plan placement of explosives within control bunker.");
        setGameText("When at Bunker, adds 1 to Deactivate The Shield Generator total for each Explosive Charge here. When Junkin uses a Concussion Grenade, all your characters are immune to that grenade and you may add or subtract 1 from weapon destiny draw.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        addPersona(Persona.JUNKIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeactivateTheShieldGeneratorTotalModifier(self, new AtCondition(self, Filters.Bunker), new HereEvaluator(self, Filters.Explosive_Charge)));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(self), Filters.character), Filters.and(Filters.Concussion_Grenade, Filters.weaponBeingFiredBy(Filters.Junkin))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.Concussion_Grenade, Filters.title("Sergeant Junkin"))) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to weapon destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            actions.add(action);

            action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Subtract 1 from weapon destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            actions.add(action);
        }
        return actions;
    }
}
