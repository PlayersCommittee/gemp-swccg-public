package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Red Leader
 */
public class Card1_029 extends AbstractRebel {
    public Card1_029() {
        super(Side.LIGHT, 1, 2, 2, 2, 5, "Red Leader", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("X-wing pilot Garven Dreis. Led Red Squadron at the Battle of Yavin, and fired an unsuccessful shot at the Death Star thermal exhaust port. Served at Dantooine Rebel base.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Red 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Adds 1 to forfeit of each other Red Squadron pilot at same location.");
        addPersona(Persona.RED_LEADER);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.RED_SQUADRON, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Red_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingRed1 = new PilotingCondition(self, Filters.Red_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingRed1, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingRed1, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.other(self), Filters.Red_Squadron_pilot, Filters.atSameLocation(self)), 1));
        return modifiers;
    }
}
