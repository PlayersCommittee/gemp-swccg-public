package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LandspeedRequiredToMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Desert
 */
public class Card6_085 extends AbstractSite {
    public Card6_085() {
        super(Side.LIGHT, "Tatooine: Desert", Title.Tatooine, Uniqueness.RESTRICTED_3);
        setLocationDarkSideGameText("Your character movement from here (except for scouts) requires +1 landspeed.");
        setLocationLightSideGameText("Your character movement from here (except for scouts) requires +1 landspeed.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DESERT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.character, Filters.except(Filters.scout)), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.character, Filters.except(Filters.scout)), playerOnLightSideOfLocation));
        return modifiers;
    }
}