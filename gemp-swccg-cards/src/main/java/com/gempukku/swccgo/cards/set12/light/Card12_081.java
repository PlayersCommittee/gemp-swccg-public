package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Theed Palace Courtyard
 */
public class Card12_081 extends AbstractSite {
    public Card12_081() {
        super(Side.LIGHT, Title.Theed_Palace_Courtyard, Title.Naboo);
        setLocationLightSideGameText("Add 1 to each of your weapon destiny draws here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.THEED_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.weapon, Filters.here(self)), 1));
        return modifiers;
    }
}