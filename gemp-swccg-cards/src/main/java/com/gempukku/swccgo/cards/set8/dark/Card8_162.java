package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Dense Forest
 */
public class Card8_162 extends AbstractSite {
    public Card8_162() {
        super(Side.DARK, "Endor: Dense Forest", Title.Endor);
        setLocationDarkSideGameText("No starships or vehicles here except speeder bikes, AT-STs, Ewok gliders and creature vehicles.");
        setLocationLightSideGameText("No starships or vehicles here except speeder bikes, AT-STs, Ewok gliders and creature vehicles.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter filter = Filters.and(Filters.or(Filters.starship, Filters.vehicle),
                Filters.except(Filters.or(Filters.speeder_bike, Filters.AT_ST, Filters.Ewok_glider, Filters.creature_vehicle)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, filter, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter filter = Filters.and(Filters.or(Filters.starship, Filters.vehicle),
                Filters.except(Filters.or(Filters.speeder_bike, Filters.AT_ST, Filters.Ewok_glider, Filters.creature_vehicle)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, filter, self));
        return modifiers;
    }
}