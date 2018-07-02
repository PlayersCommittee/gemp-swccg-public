package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
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
 * Subtype: Rebel
 * Title: Biggs Darklighter
 */
public class Card1_003 extends AbstractRebel {
    public Card1_003() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Biggs, Uniqueness.UNIQUE);
        setLore("Piloted Red 3 at Battle of Yavin. Childhood friend of Luke. Led mutiny on Rand Ecliptic and theft of Ecliptic from shipyards on Bestine. Ecliptic Evaders emblem on helmet.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Red 3, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingRed3 = new PilotingCondition(self, Filters.Red_3);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingRed3, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingRed3, 1));
        return modifiers;
    }
}
