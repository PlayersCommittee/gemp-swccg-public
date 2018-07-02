package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
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
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Reserve Pilot
 */
public class Card2_103 extends AbstractImperial {
    public Card2_103() {
        super(Side.DARK, 3, 2, 2, 1, 4, "Reserve Pilot", Uniqueness.RESTRICTED_2);
        setLore("Injuries kept Lord Vader's best TIE wingman out of the primary wave of starfighters. He remains on reserve duty, ready to replace lost pilots.");
        setGameText("Adds 1 to power of anything he pilots. When piloting Black 2, Black 3 or Black 4, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.BLACK_SQUADRON);
        setMatchingStarshipFilter(Filters.or(Filters.Black_2, Filters.Black_3, Filters.Black_4));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBlack234 = new PilotingCondition(self, Filters.or(Filters.Black_2, Filters.Black_3, Filters.Black_4));
        Filter black234Piloted = Filters.and(Filters.or(Filters.Black_2, Filters.Black_3, Filters.Black_4), Filters.hasPiloting(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ManeuverModifier(self, black234Piloted, pilotingBlack234, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBlack234, 1));
        return modifiers;
    }
}
