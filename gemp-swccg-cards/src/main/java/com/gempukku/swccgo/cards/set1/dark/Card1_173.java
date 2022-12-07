package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
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
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: DS-61-2
 */
public class Card1_173 extends AbstractImperial {
    public Card1_173() {
        super(Side.DARK, 2, 2, 2, 2, 4, "DS-61-2", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Vader's left wingman. Flies Black 2. Specially trained pilot held in reserve for mission with Vader. Nicknamed 'Mauler Mithel.'");
        setGameText("Adds 3 to power of anything he pilots. When piloting Black 2, also adds 1 to maneuver and may draw one battle destiny if not able to otherwise.");
        addPersona(Persona.DS_61_2);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BLACK_SQUADRON);
        setMatchingStarshipFilter(Filters.Black_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBlack2 = new PilotingCondition(self, Filters.Black_2);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingBlack2, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBlack2, 1));
        return modifiers;
    }
}
