package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Location
 * Subtype: Site
 * Title: Ahch To: Cliffs
 */

public class Card211_047 extends AbstractSite {
    public Card211_047() {
        super(Side.LIGHT, "Ahch-To: Cliffs", Title.Ahch_To);
        setLocationDarkSideGameText("While Luke here, attrition against you everywhere is +1. ");
        setLocationLightSideGameText("If Luke here or out of play, Force Projection is [Immune to Sense].");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {

        Condition lukeHere = new HereCondition(self, Filters.Luke);
        Condition lukeOutOfPlay = new OutOfPlayCondition(self, Filters.Luke);
        Condition lukeHereOrOutOfPlay = new OrCondition(lukeHere, lukeOutOfPlay);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.Force_Projection), lukeHereOrOutOfPlay, Title.Sense));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {

        Condition lukeHere = new HereCondition(self, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, lukeHere, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

}