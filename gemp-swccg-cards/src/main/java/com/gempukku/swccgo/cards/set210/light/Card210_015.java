package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.UnderHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Main Power Generators (1st Marker)
 */
public class Card210_015 extends AbstractSite {
    public Card210_015() {
        super(Side.LIGHT, Title.Main_Power_Generators, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLocationDarkSideGameText("Your movement to or from here requires +1 Force. If \"blown away,\" Light Side loses 8 Force.");
        setLocationLightSideGameText("'Hoth Energy Shield Rules' in effect. Your artillery weapons on Hoth are powered.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_10, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_1);
        setVirtualSuffix(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new AddToBlownAwayForceLossEffect(action, game.getLightPlayer(), 8));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostToLocationModifier(self, Filters.your(playerOnDarkSideOfLocation), 1, self));
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.your(playerOnDarkSideOfLocation), 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.artillery_weapon, Filters.on(Title.Hoth))));
        modifiers.add(new UnderHothEnergyShieldModifier(self, Filters.or(Filters.Echo_site, Filters.First_Marker, Filters.Second_Marker, Filters.Third_Marker)));
        return modifiers;
    }

}