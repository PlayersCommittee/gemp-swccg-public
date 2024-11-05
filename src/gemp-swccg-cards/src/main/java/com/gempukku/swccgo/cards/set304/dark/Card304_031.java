package com.gempukku.swccgo.cards.set304.dark;

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
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Apprentice Maverick
 */
public class Card304_031 extends AbstractImperial {
    public Card304_031() {
        super(Side.DARK, 3, 2, 1, 2, 3, "Apprentice Maverick", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Newly appointed to Praetorian Squadron Kamjin 'Maverick' Lap'lamiz has been approached by Commander Wrath to join Scholae Palatinae as a founding member and to lead Guardian Squadron.");
        setGameText("Adds 3 to power of anything he pilots. When piloting Guardian 1, also adds 2 to maneuver and may draw one battle destiny if not able to otherwise.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
		addKeywords(Keyword.LEADER, Keyword.MALE);
        setMatchingStarshipFilter(Filters.Guardian_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingGuardian1 = new PilotingCondition(self, Filters.Guardian_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingGuardian1, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingGuardian1, 1));
        return modifiers;
    }
}
