package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Blockade Flagship: Bridge
 */
public class Card12_164 extends AbstractUniqueStarshipSite {
    public Card12_164() {
        super(Side.DARK, Title.Bridge, Persona.BLOCKADE_FLAGSHIP);
        setLocationDarkSideGameText("While you control with Haako, Gunray, or Dofine, your [Presence] droids are power +1.");
        setLocationLightSideGameText("Unless your Jedi here, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.PRESENCE, Filters.droid),
                new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.or(Filters.Haako, Filters.Gunray, Filters.Dofine)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, self, new UnlessCondition(new HereCondition(self,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jedi))), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}