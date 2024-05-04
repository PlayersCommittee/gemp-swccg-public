package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Lower Corridor
 */
public class Card5_082 extends AbstractSite {
    public Card5_082() {
        super(Side.LIGHT, Title.Lower_Corridor, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLocationLightSideGameText("Your characters with lightsabers are each power +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character_with_a_lightsaber, Filters.here(self)), 2));
        return modifiers;
    }
}