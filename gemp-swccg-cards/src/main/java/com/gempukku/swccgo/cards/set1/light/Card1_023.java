package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
 * Subtype: Rebel
 * Title: Pops
 */
public class Card1_023 extends AbstractRebel {
    public Card1_023() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Pops", Uniqueness.UNIQUE);
        setLore("Piloted Gold 5 at Battle of Yavin. Real name Davish Krail. Veteran pilot. Flew fighters for two decades. Wingman of Gold Leader.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 5, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GOLD_SQUADRON);
        setMatchingStarshipFilter(Filters.Gold_5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingGold5 = new PilotingCondition(self, Filters.Gold_5);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingGold5, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingGold5, 1));
        return modifiers;
    }
}
