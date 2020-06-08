package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.ControlsWithoutCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: System
 * Title: Carida
 */
public class Card8_067 extends AbstractSystem {
    public Card8_067() {
        super(Side.LIGHT, Title.Chandrila, 4);
        setLocationDarkSideGameText("If you control with two Star Destroyers, Force drain +1 here. If you control with no capital starships, Force drain -1 here.");
        setLocationLightSideGameText("If you occupy with a capital starship, Force generation +2 for you here. Mon Mothma deploys -2 at any Chandrila location.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsWithCondition(playerOnDarkSideOfLocation, self, 2, Filters.Star_Destroyer),
                1, playerOnDarkSideOfLocation));
        modifiers.add(new ForceDrainModifier(self, new ControlsWithoutCondition(playerOnDarkSideOfLocation, self, Filters.capital_starship),
                -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.capital_starship),
                2, playerOnLightSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Mon_Mothma, -2, Filters.Chandrila_location));
        return modifiers;
    }
}