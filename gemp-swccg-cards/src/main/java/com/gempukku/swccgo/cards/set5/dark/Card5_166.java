package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalCarbonFreezingDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Carbonite Chamber
 */
public class Card5_166 extends AbstractSite {
    public Card5_166() {
        super(Side.DARK, Title.Carbonite_Chamber, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLocationDarkSideGameText("If you control, add 2 to your Carbon-Freezing destiny.");
        setLocationLightSideGameText("If you occupy, subtract 2 from opponent's Carbon-Freezing destiny.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalCarbonFreezingDestinyModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 2, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalCarbonFreezingDestinyModifier(self, new OccupiesCondition(playerOnLightSideOfLocation, self), -2, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}