package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Jendon
 */
public class Card9_105 extends AbstractImperial {
    public Card9_105() {
        super(Side.DARK, 1, 2, 2, 2, 4, "Colonel Jendon", Uniqueness.UNIQUE);
        setLore("Senior test pilot ordered to shake down first TIE defenders assigned to fleet operations. Occasionally given honor duty of flying Vader's shuttle.");
        setGameText("Adds 3 to power and 1 to maneuver of anything he pilots. When piloting Onyx 1, draws one battle destiny if not able to otherwise.");
        addPersona(Persona.JENDON);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.ONYX_SQUADRON);
        setMatchingStarshipFilter(Filters.Onyx_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Onyx_1), 1));
        return modifiers;
    }
}
