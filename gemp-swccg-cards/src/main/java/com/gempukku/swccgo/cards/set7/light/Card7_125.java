package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.AtSameOrRelatedSiteCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Spaceport City
 */
public class Card7_125 extends AbstractSite {
    public Card7_125() {
        super(Side.LIGHT, "Spaceport City", Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("May not be deployed to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4. If your thief or scout present, Force drain +1 here.");
        setLocationLightSideGameText("May not be deployed to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4. If you have a non-unique Rebel leader at same or related site, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.SPACEPORT_SITE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Kashyyyk, Title.Yavin_4, Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new PresentCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.or(Filters.thief, Filters.scout))), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new AtSameOrRelatedSiteCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.non_unique, Filters.Rebel_leader)), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}