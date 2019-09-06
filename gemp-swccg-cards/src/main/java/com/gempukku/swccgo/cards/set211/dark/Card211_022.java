package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;


public class Card211_022 extends AbstractUniqueStarshipSite {
    public Card211_022() {
        super(Side.DARK, "Invisible Hand: Hallway 328", Persona.INVISIBLE_HAND);
        setLocationDarkSideGameText("If you occupy, opponent must use +1 Force to move from here using landspeed.");
        setLocationLightSideGameText("You move to here using landspeed for free.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationUsingLandspeedModifier(self, Filters.and(Filters.your(game.getOpponent(playerOnDarkSideOfLocation)), Filters.character), new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, Filters.here(self)));
        return modifiers;

    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character), self));
        return modifiers;
    }
}