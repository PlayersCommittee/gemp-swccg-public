package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToCancelModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Endor: Chief Chirpa's Hut (v)
 */
public class Card501_040 extends AbstractSite {
    public Card501_040() {
        super(Side.LIGHT, Title.Chief_Chirpas_Hut, Title.Endor);
        setLocationDarkSideGameText("Force generation here may not be reduced by objectives.");
        setLocationLightSideGameText("You may not generate more than 2 Force at non-battlegrounds. Ewoks deploy -2 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Endor: Chief Chirpa's Hut (v)");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationImmuneToCancelModifier(self, self, Filters.Objective));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Ewok), -2, self));
        modifiers.add(new LimitForceGenerationModifier(self, Filters.non_battleground_location, 2, self.getOwner()));
        return modifiers;
    }
}