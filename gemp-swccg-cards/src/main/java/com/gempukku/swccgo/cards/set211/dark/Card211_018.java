package com.gempukku.swccgo.cards.set211.dark;

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
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

public class Card211_018 extends AbstractSite {
    /**
     * Set: Set 11
     * Type: Location
     * Subtype: Site
     * Title: Coruscant: Private Platform (Docking Bay)
     */
    public Card211_018() {
        super(Side.DARK, Title.Private_Platform, Title.Coruscant);
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force.");
        setLocationLightSideGameText("Your docking bay transit from here requires 2 Force (1 if Obi-Wan present).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_11);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self,1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourObiWanPresent = new PresentCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.ObiWan));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, new NotCondition(yourObiWanPresent), 2, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitFromCostModifier(self, yourObiWanPresent, 1, playerOnLightSideOfLocation));

        return modifiers;
    }
}