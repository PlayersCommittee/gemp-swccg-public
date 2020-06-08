package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: North Ridge (4th Marker)
 */
public class Card3_149 extends AbstractSite {
    public Card3_149() {
        super(Side.DARK, Title.North_Ridge, Title.Hoth);
        setLocationDarkSideGameText("Your AT-ATs move to here for free.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.AT_AT), self));
        return modifiers;
    }
}