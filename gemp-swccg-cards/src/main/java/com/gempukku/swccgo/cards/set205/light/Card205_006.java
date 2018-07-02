package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Lars' Moisture Farm (V)
 */
public class Card205_006 extends AbstractSite {
    public Card205_006() {
        super(Side.LIGHT, Title.Lars_Moisture_Farm, Title.Tatooine);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Unless Vader or your trooper here, Force drain -1 here (if both here, Force drain +1 instead).");
        setLocationLightSideGameText("Anakin, Beru, and Owen may move to and from here for free when using landspeed.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.VIRTUAL_SET_5);
        addKeyword(Keyword.FARM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition vaderHere = new HereCondition(self, Filters.Vader);
        Condition yourTrooperHere = new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.trooper));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new OrCondition(vaderHere, yourTrooperHere)), -1, playerOnDarkSideOfLocation));
        modifiers.add(new ForceDrainModifier(self, new AndCondition(vaderHere, yourTrooperHere), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter here = Filters.here(self);
        Filter anakinBeruAndOwen = Filters.or(Filters.Anakin, Filters.Beru, Filters.Owen);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, anakinBeruAndOwen, here));
        modifiers.add(new MovesFreeFromLocationUsingLandspeedModifier(self, anakinBeruAndOwen, here));
        return modifiers;
    }
}