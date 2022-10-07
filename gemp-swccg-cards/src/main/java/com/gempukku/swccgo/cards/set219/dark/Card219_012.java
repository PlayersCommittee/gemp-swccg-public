package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Capital City
 */
public class Card219_012 extends AbstractSite {
    public Card219_012() {
        super(Side.DARK, Title.Lothal_Capital_City, Title.Lothal);
        setLocationDarkSideGameText("Rukh, Imperials, AT-DPs, AT-STs, and speeder bikes are power and defense value +1 here.");
        setLocationLightSideGameText("While you occupy, your Imperial Complex game text is canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter filter = Filters.and(Filters.here(self), Filters.or(Filters.Imperial, Filters.Rukh, Filters.AT_ST, Filters.AT_DP, Filters.speeder_bike));
        modifiers.add(new PowerModifier(self, filter, 1));
        modifiers.add(new DefenseValueModifier(self, filter,1 ));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Lothal_Imperial_Complex, new OccupiesCondition(playerOnLightSideOfLocation, self), playerOnLightSideOfLocation));
        return modifiers;
    }
}