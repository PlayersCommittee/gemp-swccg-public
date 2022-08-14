package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 17
 * Type: Location
 * Subtype: Site
 * Title: Profundity: Docking Bay
 */
public class Card217_044 extends AbstractUniqueStarshipSite {
    public Card217_044() {
        super(Side.LIGHT, "Profundity: Docking Bay", Persona.PROFUNDITY);
        setLocationDarkSideGameText("Your docking bay transit to or from here requires +5 Force. While Vader present, gains one [Dark Side] icon.");
        setLocationLightSideGameText("Your docking bay transit from here is free.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 5, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, 5, playerOnDarkSideOfLocation));
        modifiers.add(new IconModifier(self, new PresentCondition(self, Filters.Vader), Icon.DARK_FORCE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }
}