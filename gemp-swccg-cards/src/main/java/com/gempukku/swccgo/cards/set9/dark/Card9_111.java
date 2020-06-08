package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayShuttleDirectlyFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Endicott
 */
public class Card9_111 extends AbstractImperial {
    public Card9_111() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Lieutenant Endicott", Uniqueness.UNIQUE);
        setLore("Docking bay technician. Orphan. Offered post on new Death Star when he graduated third in class from the Imperial Academy at Carida.");
        setGameText("Deploys -2 to any docking bay or launch bay. While he is at Death Star II: Docking Bay, you may shuttle for free between here and exterior sites related to system Death Star II orbits.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.docking_bay, Filters.launch_bay)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter your = Filters.your(self);
        Condition atDeathStarIIDockingBay = new AtCondition(self, Filters.Death_Star_II_Docking_Bay);
        Filter here = Filters.here(self);
        Filter exteriorSitesRelatedToOrbitedSystem = Filters.and(Filters.exterior_site, Filters.relatedSiteTo(self, Filters.isOrbitedBy(Filters.Death_Star_II_system)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayShuttleDirectlyFromLocationToLocationModifier(self, your, atDeathStarIIDockingBay, here, exteriorSitesRelatedToOrbitedSystem));
        modifiers.add(new MayShuttleDirectlyFromLocationToLocationModifier(self, your, atDeathStarIIDockingBay, exteriorSitesRelatedToOrbitedSystem, here));
        modifiers.add(new ShuttlesFreeFromLocationToLocationModifier(self, your, atDeathStarIIDockingBay, here, exteriorSitesRelatedToOrbitedSystem));
        modifiers.add(new ShuttlesFreeFromLocationToLocationModifier(self, your, atDeathStarIIDockingBay, exteriorSitesRelatedToOrbitedSystem, here));
        return modifiers;
    }
}
