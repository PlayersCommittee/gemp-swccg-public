package com.gempukku.swccgo.cards.set501.dark;

//Card501_076
//•Scarif (DS)
//DS (2): If Shield Gate here, Force drain +1 here. If Death Star in orbit, Superlaser fires for free.
//LS (1): If Shield Gate here, your shuttling and moving to or from (and deploying to) here requires +1 Force.
//[planet][parsec 7]

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.cards.conditions.OrbitingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Location
 * Subtype: System
 * Title: Scarif
 */
public class Card501_076 extends AbstractSystem {
    public Card501_076() {
        super(Side.DARK, Title.Scarif, 7);
        setLocationLightSideGameText("While Shield Gate here, your cards deploy +1 here and your movement to or from here requires +1 Force.");
        setLocationDarkSideGameText("If Shield Gate here, Force drain +1 here. While Death Star orbiting Scarif, Superlaser fires for free.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_14);
        setTestingText("Scarif");
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition shieldGateHere = new HasAttachedCondition(self, Filters.Shield_Gate);
        modifiers.add(new MoveCostToLocationModifier(self, Filters.your(playerOnLightSideOfLocation), shieldGateHere, 1, self));
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.your(playerOnLightSideOfLocation), shieldGateHere, 1, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.your(playerOnLightSideOfLocation), shieldGateHere, 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition shieldGateHere = new HasAttachedCondition(self, Filters.Shield_Gate);
        Condition deathStarInOrbitCondition = new OrbitingCondition(Filters.Death_Star_system, Title.Scarif);
        modifiers.add(new ForceDrainModifier(self, shieldGateHere, 1, playerOnDarkSideOfLocation));
        modifiers.add(new FiresForFreeModifier(self, Filters.superlaser_weapon, deathStarInOrbitCondition));
        return modifiers;
    }
}
