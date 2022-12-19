package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Tuanul Village
 */
public class Card204_031 extends AbstractSite {
    public Card204_031() {
        super(Side.LIGHT, Title.Tuanul_Village, Title.Jakku, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLocationDarkSideGameText("If your First Order character here, your total power here is +2.");
        setLocationLightSideGameText("Lor San Tekka is power +4 and defense value -2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, self, new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.First_Order_character)),
                2, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter lorSanTekkaHere = Filters.and(Filters.Lor_San_Tekka, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, lorSanTekkaHere, 4));
        modifiers.add(new DefenseValueModifier(self, lorSanTekkaHere, -2));
        return modifiers;
    }
}