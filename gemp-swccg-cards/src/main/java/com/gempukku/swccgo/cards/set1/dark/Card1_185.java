package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Tanbris
 */
public class Card1_185 extends AbstractImperial {
    public Card1_185() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Tanbris, Uniqueness.UNIQUE);
        setLore("Former fighter pilot grounded after injury. Tactical officer aboard Death Star. Competent strategist. Specializes in directing Imperial starfighters.");
        setGameText("Deploy -1 for starship weapons of any starship he pilots. Adds 2 to power of anything he pilots. Subtracts 1 from maneuver of any starfighter he pilots.");
        addIcons(Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.starship_weapon, -1, Filters.and(Filters.starship, Filters.hasPiloting(self))));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.starfighter, Filters.hasPiloting(self)), -1));
        return modifiers;
    }
}
