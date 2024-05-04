package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: System
 * Title: Mon Calamari
 */
public class Card9_149 extends AbstractSystem {
    public Card9_149() {
        super(Side.DARK, Title.Mon_Calamari, 6, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationDarkSideGameText("If you occupy with a Star Destroyer, opponent's Star Cruisers are deploy +2 (and may not deploy here).");
        setLocationLightSideGameText("Force Drain -1 here. If you occupy with a Star Cruiser, opponent's Star Destroyers deploy +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition occupyWithStarDestroyer = new OccupiesWithCondition(playerOnDarkSideOfLocation, self, Filters.Star_Destroyer);
        Filter opponentsStarCruisers = Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.Star_Cruiser);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, opponentsStarCruisers, occupyWithStarDestroyer, 2));
        modifiers.add(new MayNotDeployToLocationModifier(self, opponentsStarCruisers, occupyWithStarDestroyer, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Star_Destroyer),
                new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.Star_Cruiser), 2, self));
        return modifiers;
    }
}