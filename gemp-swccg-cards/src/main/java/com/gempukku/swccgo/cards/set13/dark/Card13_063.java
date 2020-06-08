package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Crossfire
 */
public class Card13_063 extends AbstractDefensiveShield {
    public Card13_063() {
        super(Side.DARK, Title.Crossfire);
        setLore("Scout walker pilots are trained to set up a deadly heavy fire zone. This tactic can be disrupted by enemy weapons fire.");
        setGameText("Plays on table. S-foils and Maneuvering Flaps are suspended where you have either a weapon present or a starship (or vehicle) with maneuver > 3 present.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.locationAndCardsAtLocation(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.or(Filters.weapon,
                Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.maneuverMoreThan(3))))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendModifierEffectsModifier(self, filter, Filters.or(Filters.S_foils, Filters.Maneuvering_Flaps)));
        return modifiers;
    }
}