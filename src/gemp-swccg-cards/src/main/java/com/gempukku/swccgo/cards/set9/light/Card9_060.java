package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: System
 * Title: Sullust
 */
public class Card9_060 extends AbstractSystem {
    public Card9_060() {
        super(Side.LIGHT, Title.Sullust, 7, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationDarkSideGameText("Your starships deploy +1 here.");
        setLocationLightSideGameText("To move your starship between here and Death Star II, system it orbits, or Mon Calamari requires -1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DEATH_STAR_II, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship), 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarship = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);
        Filter otherSystems = Filters.or(Filters.Death_Star_II_system, Filters.isOrbitedBy(Filters.Death_Star_II_system), Filters.Mon_Calamari_system);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, yourStarship, -1, self, otherSystems));
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, yourStarship, -1, otherSystems, self));
        return modifiers;
    }
}