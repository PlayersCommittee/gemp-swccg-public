package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: Sector
 * Title: Bespin: Cloud City
 */
public class Card5_077 extends AbstractSector {
    public Card5_077() {
        super(Side.LIGHT, Title.Bespin_Cloud_City, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLocationDarkSideGameText("If you control, for each of your starships or vehicles here, your total power is +1 in battles at Cloud City sites.");
        setLocationLightSideGameText("If you control, for each of your starships or vehicles here, your total power is +1 in battles at Cloud City sites.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLOUD_CITY, Icon.PLANET);
        addKeywords(Keyword.CLOUD_CITY_LOCATION, Keyword.CLOUD_SECTOR);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Cloud_City_site, Filters.battleLocation),
                new ControlsCondition(playerOnDarkSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.starship, Filters.vehicle))),
                playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Cloud_City_site, Filters.battleLocation),
                new ControlsCondition(playerOnLightSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.starship, Filters.vehicle))),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}