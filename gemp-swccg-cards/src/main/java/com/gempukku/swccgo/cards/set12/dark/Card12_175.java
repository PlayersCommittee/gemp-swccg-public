package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationUsingHyperspeedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: System
 * Title: Tatooine
 */
public class Card12_175 extends AbstractSystem {
    public Card12_175() {
        super(Side.DARK, Title.Tatooine, 7);
        setLocationDarkSideGameText("While you occupy, opponent's starship movement from here requires +1 Force. If Maul here, Force drain +1 here.");
        setLocationLightSideGameText("Your movement to here using hyperspeed requires -1 Force.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.starship),
                new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, self));
        modifiers.add(new ForceDrainModifier(self, new HereCondition(self, Filters.Maul), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostToLocationUsingHyperspeedModifier(self, Filters.your(playerOnLightSideOfLocation), -1, self));
        return modifiers;
    }
}