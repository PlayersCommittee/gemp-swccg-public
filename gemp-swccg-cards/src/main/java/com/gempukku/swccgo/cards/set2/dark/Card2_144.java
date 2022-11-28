package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEpicEventModifierEffect;
import com.gempukku.swccgo.logic.modifiers.CommencePrimaryIgnitionTotalModifier;
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
 * Title: Death Star: Conference Room
 */
public class Card2_144 extends AbstractSite {
    public Card2_144() {
        super(Side.DARK, Title.Death_Star_Conference_Room, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLocationDarkSideGameText("If you control, with a leader here, may add 1 to total of Commence Primary Ignition.");
        setLocationLightSideGameText("If you control, Force drain +1 and may subtract 1 from total of Commence Primary Ignition.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.calculatingEpicEventTotal(game, effectResult, Filters.Commence_Primary_Ignition)
                && GameConditions.controlsWith(game, playerOnDarkSideOfLocation, self, Filters.leader)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Add 1 to Commence Primary Ignition total");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfEpicEventModifierEffect(action,
                            new CommencePrimaryIgnitionTotalModifier(self, 1), "Adds 1 to Commence Primary Ignition total"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.calculatingEpicEventTotal(game, effectResult, Filters.Commence_Primary_Ignition)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Subtract 1 to Commence Primary Ignition total");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfEpicEventModifierEffect(action,
                            new CommencePrimaryIgnitionTotalModifier(self, -1), "Subtracts 1 to Commence Primary Ignition total"));
            return Collections.singletonList(action);
        }
        return null;
    }
}