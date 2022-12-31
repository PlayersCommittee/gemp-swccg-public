package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Rebel
 * Title: Tiree
 */
public class Card2_021 extends AbstractRebel {
    public Card2_021() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Tiree", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Piloted Gold 2 as defensive cover for Gold Leader during attack run in Death Star trench at the Battle of Yavin.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 2, also adds 1 to maneuver and (when in Death Star: Trench) adds 1 to total of Attack Run.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GOLD_SQUADRON);
        setMatchingStarshipFilter(Filters.Gold_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingGold2 = new PilotingCondition(self, Filters.Gold_2);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingGold2, 1));
        modifiers.add(new AttackRunTotalModifier(self, new AndCondition(pilotingGold2, new AtCondition(self, Filters.Death_Star_Trench)), 1));
        return modifiers;
    }
}
