package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Location
 * Subtype: Site
 * Title: Xizor's Palace: Uplink Station
 */
public class Card203_034 extends AbstractSite {
    public Card203_034() {
        super(Side.DARK, Title.Uplink_Station, Title.Coruscant);
        setLocationDarkSideGameText("If Falleen's Fist at Coruscant and your Black Sun agent here, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.XIZORS_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new AndCondition(new AtCondition(self, Filters.Falleens_Fist, Title.Coruscant),
                new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Black_Sun_agent))), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}