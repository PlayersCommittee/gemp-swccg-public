package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Lieutenant Rya Kirsch
 */
public class Card14_017 extends AbstractRepublic {
    public Card14_017() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Lieutenant Rya Kirsch", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Became a member of Bravo Flight after embarrassing himself in front of Amidala as a guard. Has redeemed himself by becoming an expert in defense aviation tactics.");
        setGameText("Adds 2 to power of anything he pilots. While aboard Bravo 4, draws one battle destiny if not able to otherwise, and opponent's droid starfighters are deploy +2 at same system.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.BRAVO_SQUADRON);
        setMatchingStarshipFilter(Filters.Bravo_4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBravo4 = new PilotingCondition(self, Filters.Bravo_4);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBravo4, 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.droid_starfighter),
                pilotingBravo4, 2, Filters.sameSystem(self)));
        return modifiers;
    }
}
