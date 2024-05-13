package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Location
 * Subtype: Site
 * Title: Arx: The Iron Garage
 */
public class Card302_019 extends AbstractSite {
    public Card302_019() {
        super(Side.LIGHT, Title.Iron_Garage, Title.Arx, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLocationLightSideGameText("Your vehicles are each power +2 here. Force drain +1 here.");
        setLocationDarkSideGameText("Your combat vehicles and Imperial troopers are each forfeit +2 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter powerPlusOneHere = Filters.or(Filters.vehicle);
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), powerPlusOneHere, Filters.here(self)), 2));
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter forfeitPlusOneHere = Filters.or(Filters.combat_vehicle, Filters.and(Filters.Imperial, Filters.trooper));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), forfeitPlusOneHere, Filters.here(self)), 2));
        return modifiers;
    }
}
