package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UnderHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Main Power Generators (1st Marker)
 */
public class Card3_061 extends AbstractSite {
    public Card3_061() {
        super(Side.LIGHT, Title.Main_Power_Generators, Title.Hoth);
        setLocationDarkSideGameText("'Hoth Energy Shield Rules' in effect. If 'blown away,' Light Side loses 8 Force.");
        setLocationLightSideGameText("'Hoth Energy Shield Rules' in effect. Your artillery weapons here are powered.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_1);
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
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.artillery_weapon, Filters.here(self))));
        modifiers.add(new UnderHothEnergyShieldModifier(self, Filters.or(Filters.Echo_site, Filters.First_Marker, Filters.Second_Marker, Filters.Third_Marker)));
        return modifiers;
    }
}