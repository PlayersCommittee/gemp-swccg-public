package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Location
 * Subtype: Site
 * Title: Blockade Flagship: Docking Bay
 */
public class Card14_111 extends AbstractUniqueStarshipSite {
    public Card14_111() {
        super(Side.DARK, "Blockade Flagship: Docking Bay", Persona.BLOCKADE_FLAGSHIP);
        setLocationDarkSideGameText("Your Docking Bay transit from here is free.");
        setLocationLightSideGameText("Your Docking Bay transit to and from here requires +8 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitToCostModifier(self, 8, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitFromCostModifier(self, 8, playerOnLightSideOfLocation));
        return modifiers;
    }
}