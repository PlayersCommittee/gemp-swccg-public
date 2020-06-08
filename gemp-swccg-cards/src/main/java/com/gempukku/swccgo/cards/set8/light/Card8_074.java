package com.gempukku.swccgo.cards.set8.light;

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
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Great Forest
 */
public class Card8_074 extends AbstractSite {
    public Card8_074() {
        super(Side.LIGHT, "Endor: Great Forest", Title.Endor, Uniqueness.RESTRICTED_3);
        setLocationDarkSideGameText("Your character movement from here (except for Yuzzum and scouts) requires +1 landspeed.");
        setLocationLightSideGameText("Your character movement from here (except for Ewoks and scouts) requires +1 landspeed.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.character, Filters.except(Filters.or(Filters.Yuzzum, Filters.scout))), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedRequiredToMoveFromLocationModifier(self, 1, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.character, Filters.except(Filters.or(Filters.Ewok, Filters.scout))), playerOnLightSideOfLocation));
        return modifiers;
    }
}