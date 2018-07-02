package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Spaceport Prefect's Office
 */
public class Card7_290 extends AbstractSite {
    public Card7_290() {
        super(Side.DARK, "Spaceport Prefect's Office", Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4. If your Imperial leader here, Imperials at same and related sites are power and forfeit +1.");
        setLocationLightSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.SPACEPORT_SITE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Kashyyyk, Title.Yavin_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourImperialLeaderHere = new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Imperial_leader));
        Filter imperialAtSameAndRelatedSites = Filters.and(Filters.Imperial, Filters.atSameOrAdjacentSite(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, imperialAtSameAndRelatedSites, yourImperialLeaderHere, 1));
        modifiers.add(new ForfeitModifier(self, imperialAtSameAndRelatedSites, yourImperialLeaderHere, 1));
        return modifiers;
    }
}