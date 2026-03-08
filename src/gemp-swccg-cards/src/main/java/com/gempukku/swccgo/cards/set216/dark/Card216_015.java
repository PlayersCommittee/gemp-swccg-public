package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Citadel Tower
 */
public class Card216_015 extends AbstractSite {
    public Card216_015() {
        super(Side.DARK, Title.Scarif_Citadel_Tower, Title.Scarif, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLocationDarkSideGameText("If Krennic or a Death Trooper here, Force drain +1 here.");
        setLocationLightSideGameText("If Cassian, Jyn, or K-2SO here, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new HereCondition(self, Filters.or(Filters.Krennic, Filters.death_trooper)), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new HereCondition(self, Filters.or(Filters.Cassian, Filters.Jyn, Filters.K2SO)), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}
