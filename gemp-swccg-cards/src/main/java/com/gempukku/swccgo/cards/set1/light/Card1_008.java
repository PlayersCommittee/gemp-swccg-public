package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Dutch
 */
public class Card1_008 extends AbstractRebel {
    public Card1_008() {
        super(Side.LIGHT, 1, 2, 2, 2, 5, "Dutch", Uniqueness.UNIQUE);
        setLore("Jon 'Dutch' Vander. Gold Squadron leader at Battle of Yavin. Prefers Y-wing fighters. Previously led squadron at Renforra Base. Emblem of Specter Squadron on his helmet.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Adds 1 to forfeit of each other Gold Squadron pilot at same location.");
        addPersona(Persona.DUTCH);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GOLD_SQUADRON, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Gold_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingGold1 = new PilotingCondition(self, Filters.Gold_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingGold1, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingGold1, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.other(self), Filters.Gold_Squadron_Pilot, Filters.atSameLocation(self)), 1));
        return modifiers;
    }
}
