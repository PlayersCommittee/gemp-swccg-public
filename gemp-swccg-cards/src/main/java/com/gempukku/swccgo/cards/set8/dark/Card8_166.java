package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Landing Platform (Docking Bay)
 */
public class Card8_166 extends AbstractSite {
    public Card8_166() {
        super(Side.DARK, Title.Landing_Platform, Title.Endor);
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force (free if your Imperial present).");
        setLocationLightSideGameText("Your docking bay transit from here requires 7 Force. Your transit to here requires +9 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourImperialPresent = new PresentCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Imperial));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, new NotCondition(yourImperialPresent), 1, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, yourImperialPresent, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 7, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 9, playerOnLightSideOfLocation));
        return modifiers;
    }
}