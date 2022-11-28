package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Docking Bay
 */
public class Card1_297 extends AbstractSite {
    public Card1_297() {
        super(Side.DARK, Title.Yavin_4_Docking_Bay, Title.Yavin_4, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C2);
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force.");
        setLocationLightSideGameText("Your docking bay transit from here requires 1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}