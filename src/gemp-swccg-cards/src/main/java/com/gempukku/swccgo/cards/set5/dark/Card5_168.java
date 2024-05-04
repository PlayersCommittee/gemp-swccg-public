package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DoubledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Dining Room
 */
public class Card5_168 extends AbstractSite {
    public Card5_168() {
        super(Side.DARK, Title.Dining_Room, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLocationDarkSideGameText("Your characters and character weapons may deploy here as a 'react.'");
        setLocationLightSideGameText("If you control, Blue Milk, Beru Stew and Yoda Stew are doubled. Jek is deploy -2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // TODO: Fix this
        // modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
        //        Filters.or(Filters.character, Filters.character_weapon)), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DoubledModifier(self, Filters.or(Filters.Blue_Milk, Filters.Beru_Stew, Filters.Yoda_Stew),
                new ControlsCondition(playerOnLightSideOfLocation, self)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Jek, -2, self));
        return modifiers;
    }
}