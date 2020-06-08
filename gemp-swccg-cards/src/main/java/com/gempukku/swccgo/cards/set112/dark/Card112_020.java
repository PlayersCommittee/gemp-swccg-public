package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LandspeedRequiredToMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Desert Heart
 */
public class Card112_020 extends AbstractSite {
    public Card112_020() {
        super(Side.DARK, Title.Desert_Heart, Title.Tatooine);
        setLocationDarkSideGameText("Your character movement from here (except for Jawas) requires +1 landspeed.");
        setLocationLightSideGameText("Your character movement from here (except for Jawas) requires +1 landspeed.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DESERT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.character, Filters.except(Filters.Jawa)), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.character, Filters.except(Filters.Jawa)), playerOnLightSideOfLocation));
        return modifiers;
    }
}