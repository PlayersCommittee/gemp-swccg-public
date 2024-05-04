package com.gempukku.swccgo.cards.set220.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.CommuningCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Theed Palace Generator (V)
 */
public class Card220_008 extends AbstractSite {
    public Card220_008() {
        super(Side.LIGHT, Title.Theed_Palace_Generator, Title.Naboo, Uniqueness.UNIQUE, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Dark Jedi are power +1 here. Jedi are power +2 here. Force drain -1 here.");
        setLocationLightSideGameText("While Qui-Gon 'communing,' opponent's [Reflections II] objective targets Anakin instead of Luke.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_20);
        addKeywords(Keyword.THEED_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Dark_Jedi, Filters.here(self)), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Jedi, Filters.here(self)), 2));
        modifiers.add(new ForceDrainModifier(self, -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Icon.REFLECTIONS_II, Filters.Objective), new CommuningCondition(Filters.QuiGon), ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_ANAKIN_INSTEAD_OF_LUKE));
        return modifiers;
    }
}