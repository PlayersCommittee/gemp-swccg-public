package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
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
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Ewok Village
 */
public class Card8_163 extends AbstractSite {
    public Card8_163() {
        super(Side.DARK, Title.Ewok_Village, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLocationDarkSideGameText("No starships or vehicles here. If no Ewoks on Endor, Force drain +1 here.");
        setLocationLightSideGameText("No starships or vehicles here. If your Ewok present, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter starshipsOrVehicles = Filters.or(Filters.starship, Filters.vehicle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, starshipsOrVehicles, self));
        modifiers.add(new ForceDrainModifier(self, new CantSpotCondition(self, Filters.and(Filters.Ewok, Filters.on(Title.Endor))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter starshipsOrVehicles = Filters.or(Filters.starship, Filters.vehicle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, starshipsOrVehicles, self));
        modifiers.add(new ForceDrainModifier(self, new PresentCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Ewok)),
                1, playerOnLightSideOfLocation));
        return modifiers;
    }
}