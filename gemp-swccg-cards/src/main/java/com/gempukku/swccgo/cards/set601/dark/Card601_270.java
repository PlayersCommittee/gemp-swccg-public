package com.gempukku.swccgo.cards.set601.dark;

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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Location
 * Subtype: Site
 * Title: Ralltiir: Supply Route
 */
public class Card601_270 extends AbstractSite {
    public Card601_270() {
        super(Side.DARK, "Ralltiir: Supply Route", Title.Ralltiir, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLocationDarkSideGameText("Your combat vehicles and Imperial troopers deploy -1 here.");
        setLocationLightSideGameText("May add 1 to the forfeit of your warriors here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.combat_vehicle, Filters.and(Filters.Imperial, Filters.trooper))), -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourWarriorsHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.warrior, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, yourWarriorsHere, 1));
        return modifiers;
    }
}