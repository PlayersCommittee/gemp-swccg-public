package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.EscortingCaptiveCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Boba Fett
 */
public class Card5_091 extends AbstractAlien {
    public Card5_091() {
        super(Side.DARK, 1, 5, 4, 3, 6, "Boba Fett", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Feared bounty hunter. Collected bounties on Solo from both the Empire and Jabba the Hutt. Took exquisite pleasure in using Solo's friend to capture him.");
        setGameText("Adds 3 to power of anything he pilots. When piloting Slave I, also adds 2 to maneuver and may draw one battle destiny if not able to otherwise. When escorting a captive, captive is forfeit +5. May 'fly' (landspeed = 3). Immune to attrition < 3.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setMatchingStarshipFilter(Filters.Slave_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Slave_I, Filters.hasPiloting(self)), 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Slave_I), 1));
        modifiers.add(new ForfeitModifier(self, Filters.escortedBy(self), new EscortingCaptiveCondition(self), 5));
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
