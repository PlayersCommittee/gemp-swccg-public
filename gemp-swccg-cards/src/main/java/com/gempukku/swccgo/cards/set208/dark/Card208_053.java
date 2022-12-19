package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Starkiller Base: Forest
 */
public class Card208_053 extends AbstractSite {
    public Card208_053() {
        super(Side.DARK, "Starkiller Base: Forest", Title.Starkiller_Base, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setLocationDarkSideGameText("While Kylo armed with a lightsaber here, he is power +2.");
        setLocationLightSideGameText("While Rey armed with a lightsaber here, she is power +2.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Kylo, Filters.armedWith(Filters.lightsaber), Filters.here(self)), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rey, Filters.armedWith(Filters.lightsaber), Filters.here(self)), 2));
        return modifiers;
    }
}