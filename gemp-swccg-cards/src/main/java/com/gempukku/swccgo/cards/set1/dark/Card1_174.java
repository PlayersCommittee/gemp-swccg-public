package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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
 * Title: DS-61-3
 */
public class Card1_174 extends AbstractImperial {
    public Card1_174() {
        super(Side.DARK, 2, 2, 3, 2, 3, "DS-61-3", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Vader's right wingman. Flies Black 3. Reputation for ferocity in combat. Corellian pilot with excellent skills. Nicknamed 'Backstabber.'");
        setGameText("Adds 3 to power of anything he pilots. When piloting Black 3, also adds 1 to maneuver and may draw one battle destiny if not able to otherwise.");
        addPersona(Persona.DS_61_3);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BLACK_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Black_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBlack3 = new PilotingCondition(self, Filters.Black_3);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingBlack3, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBlack3, 1));
        return modifiers;
    }
}
