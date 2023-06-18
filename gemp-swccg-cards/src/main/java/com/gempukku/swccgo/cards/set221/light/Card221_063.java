package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Badlands Of N'g'zi
 */
public class Card221_063 extends AbstractSite {
    public Card221_063() {
        super(Side.LIGHT, "Geonosis: Badlands Of N'g'zi", Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Your [Separatist] characters and [Separatist] vehicles are power +1 here.");
        setLocationLightSideGameText("Your [Clone Army] characters and [Clone Army] vehicles are power +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.SEPARATIST, Filters.or(Filters.character, Filters.vehicle), Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Icon.CLONE_ARMY, Filters.or(Filters.character, Filters.vehicle), Filters.here(self)), 1));
        return modifiers;
    }
}