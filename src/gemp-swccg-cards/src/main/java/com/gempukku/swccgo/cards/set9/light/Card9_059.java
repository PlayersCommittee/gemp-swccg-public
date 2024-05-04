package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithoutCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
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
public class Card9_059 extends AbstractSystem {
    public Card9_059() {
        super(Side.LIGHT, Title.Mon_Calamari, 6, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationDarkSideGameText("If you control without a Star Destroyer, Force drain -1 here.");
        setLocationLightSideGameText("If you occupy with a Star Cruiser, opponent's Star Destroyers may not deploy here. Your Star Cruisers deploy -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DEATH_STAR_II, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsWithoutCondition(playerOnDarkSideOfLocation, self, Filters.Star_Destroyer),
                -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Star_Destroyer),
                new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.Star_Cruiser), self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Star_Cruiser), -1, self));
        return modifiers;
    }
}