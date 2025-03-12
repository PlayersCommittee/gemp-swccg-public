package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Location
 * Subtype: Site
 * Title: Arx: Eos City
 */
public class Card302_016 extends AbstractSite {
    public Card302_016() {
        super(Side.DARK, Title.Eos_City, Title.Arx, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLocationDarkSideGameText("Councilors deploy -4 here. If your Councilor here, all Imperials are deploy -1 at sites.");
        setLocationLightSideGameText("Force drain +1 here. If you control, Councilors may not deploy to Arx.");
        addIcon(Icon.DARK_FORCE, 2);
		addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Dark_Councilor, -4, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial,
                new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Dark_Councilor)), -1, Filters.site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Dark_Councilor,
                new ControlsCondition(playerOnLightSideOfLocation, self), Filters.Arx_location));
        return modifiers;
    }
}