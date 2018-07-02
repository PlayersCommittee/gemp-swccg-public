package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEpicEventModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Briefing Room
 */
public class Card2_067 extends AbstractSite {
    public Card2_067() {
        super(Side.LIGHT, "Yavin 4: Briefing Room", Title.Yavin_4);
        setLocationDarkSideGameText("If you control, Force drain +1 here and subtract 1 from total of Attack Run.");
        setLocationLightSideGameText("If you control, with a leader here, may add 1 to total of Attack Run.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        modifiers.add(new AttackRunTotalModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.calculatingEpicEventTotal(game, effectResult, Filters.Attack_Run)
                && GameConditions.controlsWith(game, playerOnLightSideOfLocation, self, Filters.leader)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Add 1 to Attack Run total");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfEpicEventModifierEffect(action,
                            new AttackRunTotalModifier(self, 1), "Adds 1 to Attack Run total"));
            return Collections.singletonList(action);
        }
        return null;
    }
}