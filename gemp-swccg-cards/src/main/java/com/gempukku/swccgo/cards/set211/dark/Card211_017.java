package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

public class Card211_017 extends AbstractSite {
    /**
     * Set: Set 11
     * Type: Location
     * Subtype: Site
     * Title: Coruscant: 500 Republica
     */
    public Card211_017() {
        super(Side.DARK, "Coruscant: 500 Republica", Title.Coruscant);
        setLocationDarkSideGameText("Grievous moves from here for free.");
        setLocationLightSideGameText("May not be separated from Private Platform.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeFromLocationModifier(self, Filters.Grievous, self));
        return modifiers;
    }
}