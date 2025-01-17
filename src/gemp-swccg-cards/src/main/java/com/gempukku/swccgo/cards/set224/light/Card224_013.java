package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Lower Corridor (V)
 */
public class Card224_013 extends AbstractSite {
    public Card224_013() {
        super(Side.LIGHT, Title.Lower_Corridor, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLocationDarkSideGameText("May not be converted. While Luke or Rey alone here, your Interrupts are destiny -1.");
        setLocationLightSideGameText("While Luke alone here, [Cloud City] Rebels are destiny, power, and forfeit +1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourInterrupts = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Interrupt);
        Condition lukeOrReyAloneHere = new HereCondition(self, Filters.and(Filters.or(Filters.Luke, Filters.Rey), Filters.alone));

        modifiers.add(new MayNotBeConvertedModifier(self));
        modifiers.add(new DestinyModifier(self, yourInterrupts, lukeOrReyAloneHere, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter cloudCityRebels = Filters.and(Icon.CLOUD_CITY, Filters.Rebel);
        Condition lukeAloneHere = new HereCondition(self, Filters.and(Filters.Luke, Filters.alone));
        modifiers.add(new DestinyModifier(self, cloudCityRebels, lukeAloneHere, 1));
        modifiers.add(new PowerModifier(self, cloudCityRebels, lukeAloneHere, 1));
        modifiers.add(new ForfeitModifier(self, cloudCityRebels, lukeAloneHere, 1));
        return modifiers;
    }    
}