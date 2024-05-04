package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Jabba's Palace: Droid Workshop
 */
public class Card6_163 extends AbstractSite {
    public Card6_163() {
        super(Side.DARK, Title.Droid_Workshop, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLocationDarkSideGameText("Your droids deploy -1 here.");
        setLocationLightSideGameText("If you control, with a droid here, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.JABBAS_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.droid), -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.droid), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}