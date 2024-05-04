package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Sacred Falls (Forest)
 */
public class Card216_032 extends AbstractSite {
    public Card216_032() {
        super(Side.LIGHT, "Kashyyyk: Sacred Falls (Forest)", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLocationDarkSideGameText("While opponent occupies with a Wookiee, your Force generation here is canceled.");
        setLocationLightSideGameText("Your Wookiees move from here for free when using landspeed.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_16, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new GenerateNoForceModifier(self, self, new OccupiesWithCondition(game.getOpponent(playerOnDarkSideOfLocation), self, Filters.Wookiee), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MovesFreeFromLocationUsingLandspeedModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Wookiee), self));
        return modifiers;
    }
}
