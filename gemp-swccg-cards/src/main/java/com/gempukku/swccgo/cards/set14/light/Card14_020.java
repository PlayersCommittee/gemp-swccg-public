package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Officer Dolphe
 */
public class Card14_020 extends AbstractRepublic {
    public Card14_020() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Dolphe, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Growing up in a small town outside of Theed, Porro became adept at speeder control and thrust. His decision to volunteer for Bravo Flight made Ric Olie a very happy man.");
        setGameText("Adds 2 to power of anything he pilots. While aboard Bravo 2, draws one battle destiny if not able to otherwise, and opponent's battle destiny draws are -2 at same system.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.BRAVO_SQUADRON);
        setMatchingStarshipFilter(Filters.Bravo_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBravo2 = new PilotingCondition(self, Filters.Bravo_2);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBravo2, 1));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.sameSystem(self), pilotingBravo2, -2, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
