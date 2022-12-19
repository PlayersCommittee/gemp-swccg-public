package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Forest Depths
 */
public class Card215_013 extends AbstractSite {
    public Card215_013() {
        super(Side.LIGHT, "Kashyyyk: Forest Depths", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLocationDarkSideGameText("No starships or vehicles here.");
        setLocationLightSideGameText("While you control, Wookiees are destiny +1.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_15, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotExistAtLocationModifier(self, Filters.or(Filters.starship, Filters.vehicle), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DestinyModifier(self, Filters.Wookiee, new ControlsCondition(playerOnLightSideOfLocation, self), 1));
        return modifiers;
    }
}
