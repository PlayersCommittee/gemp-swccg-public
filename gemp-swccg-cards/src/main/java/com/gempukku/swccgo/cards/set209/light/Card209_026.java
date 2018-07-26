package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Landing Pad Nine (Docking Bay)
 */


public class Card209_026 extends AbstractSite {
    public Card209_026() {
        super(Side.LIGHT, Title.Scarif_Landing_Pad_Nine, Title.Scarif);
        setLocationDarkSideGameText("Your docking bay transit from here requires 2 Force. Your combat vehicles deploy +1 here.");
        setLocationLightSideGameText("Your docking bay transit to or from here requires +1 Force (+4 Force if Shield Gate on table).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.DOCKING_BAY);
    }


    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.combat_vehicle, 1, Filters.Scarif_Docking_Bay));

        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Shield Gate doesn't exist as of Set 9, this is future proofing.
        if (GameConditions.canSpot(game, self, Filters.Shield_Gate)) {
            modifiers.add(new DockingBayTransitFromCostModifier(self, 4, playerOnLightSideOfLocation));
            modifiers.add(new DockingBayTransitToCostModifier(self, 4, playerOnLightSideOfLocation));
        }
        else {
            modifiers.add(new DockingBayTransitFromCostModifier(self, 1, playerOnLightSideOfLocation));
            modifiers.add(new DockingBayTransitToCostModifier(self, 1, playerOnLightSideOfLocation));
        }

        return modifiers;
    }
}