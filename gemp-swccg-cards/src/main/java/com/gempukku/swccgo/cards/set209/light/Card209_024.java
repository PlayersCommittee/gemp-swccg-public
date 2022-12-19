package com.gempukku.swccgo.cards.set209.light;

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
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Beach
 */
public class Card209_024 extends AbstractSite {
    public Card209_024() {
        super(Side.LIGHT, "Scarif: Beach", Title.Scarif, Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLocationLightSideGameText("Your combat vehicles, scouts, and spies are each power +1 here. Force drain +1 here.");
        setLocationDarkSideGameText("Your combat vehicles and Imperial troopers are each forfeit +1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter powerPlusOneHere = Filters.or(Filters.combat_vehicle, Filters.scout, Filters.spy);
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), powerPlusOneHere, Filters.here(self)), 1));
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter forfeitPlusOneHere = Filters.or(Filters.combat_vehicle, Filters.and(Filters.Imperial, Filters.trooper));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), forfeitPlusOneHere, Filters.here(self)), 1));
        return modifiers;
    }
}
