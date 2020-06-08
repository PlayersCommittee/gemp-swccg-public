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
 * Title: Jek Porkins
 */
public class Card1_013 extends AbstractRebel {
    public Card1_013() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Jek, Uniqueness.UNIQUE);
        setLore("Piloted Red 6 at Battle of Yavin. Specializes in strafing runs. Served in Tierfon Yellow Aces squadron at Tierfon Rebel Outpost. Free trader from Bestine system.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Red 6, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_6);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingRed6 = new PilotingCondition(self, Filters.Red_6);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingRed6, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingRed6, 1));
        return modifiers;
    }
}
