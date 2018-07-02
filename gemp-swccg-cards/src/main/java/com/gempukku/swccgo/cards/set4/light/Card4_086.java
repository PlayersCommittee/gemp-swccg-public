package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Jungle
 */
public class Card4_086 extends AbstractSite {
    public Card4_086() {
        super(Side.LIGHT, Title.Dagobah_Jungle, Title.Dagobah);
        setLocationDarkSideGameText("No starships or vehicles here. If you occupy, Force generation +1 for you here.");
        setLocationLightSideGameText("No starships or vehicles here. Your creatures are deploy -2 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.JUNGLE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter starshipsOrVehicles = Filters.or(Filters.starship, Filters.vehicle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, starshipsOrVehicles, self));
        modifiers.add(new ForceGenerationModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter starshipsOrVehicles = Filters.or(Filters.starship, Filters.vehicle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, starshipsOrVehicles, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.creature), -2, self));
        return modifiers;
    }
}