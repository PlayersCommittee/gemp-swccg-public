package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
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
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Petranaki Arena
 */
public class Card227_010 extends AbstractSite {
    public Card227_010() {
        super(Side.DARK, Title.Geonosis_Petranaki_Arena, Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLocationDarkSideGameText("If your [Separatist] leader here, Force drain +1 here.");
        setLocationLightSideGameText("During battle here, you must first use 1 Force to play an Interrupt.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.SEPARATIST, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.SEPARATIST, Filters.leader)), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Interrupt),
            new DuringBattleAtCondition(Filters.here(self)), 1));
        return modifiers;
    }
}
