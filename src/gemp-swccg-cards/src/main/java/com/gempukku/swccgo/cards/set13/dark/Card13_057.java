package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Location
 * Subtype: Site
 * Title: Blockade Flagship: Hallway
 */
public class Card13_057 extends AbstractUniqueStarshipSite {
    public Card13_057() {
        super(Side.DARK, "Blockade Flagship: Hallway", Persona.BLOCKADE_FLAGSHIP, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLocationDarkSideGameText("If you occupy, Force generation +1 here, and opponent's characters may not move from here.");
        setLocationLightSideGameText("Your Jedi deploy +2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youOccupy = new OccupiesCondition(playerOnDarkSideOfLocation, self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, youOccupy, 1, playerOnDarkSideOfLocation));
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.character),
                youOccupy, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jedi), 2, self));
        return modifiers;
    }
}