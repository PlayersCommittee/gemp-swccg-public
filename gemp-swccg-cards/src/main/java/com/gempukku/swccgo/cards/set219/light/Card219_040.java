package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Comm Tower E-272 (Ezra's Roost)
 */
public class Card219_040 extends AbstractSite {
    public Card219_040() {
        super(Side.LIGHT, "Lothal: Comm Tower E-272 (Ezra's Roost)", Title.Lothal);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("Ezra deploys -1 (and is immune to attrition) here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Ezra, -1, self));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.and(Filters.here(self), Filters.Ezra)));
        return modifiers;
    }
}
