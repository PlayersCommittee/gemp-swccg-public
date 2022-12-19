package com.gempukku.swccgo.cards.set601.light;

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
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Location
 * Subtype: Site
 * Title: Dantooine: Base - Docking Bay
 */
public class Card601_256 extends AbstractSite {
    public Card601_256() {
        super(Side.LIGHT, "Dantooine: Base - Docking Bay", Title.Dantooine, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLocationDarkSideGameText("Your docking bay transit from here requires 2 Force.");
        setLocationLightSideGameText("Your docking bay transit from here is free.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4);
        addKeywords(Keyword.DOCKING_BAY);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }
}