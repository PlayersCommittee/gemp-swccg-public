package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: AT-ST Pilot
 */
public class Card8_091 extends AbstractImperial {
    public Card8_091() {
        super(Side.DARK, 3, 2, 1, 2, 3, "AT-ST Pilot", Uniqueness.RESTRICTED_3);
        setLore("Due to the unstable control characteristics of AT-STs, only the most talented recruits are assigned to them.");
        setGameText("Adds 2 to power of any combat vehicle he pilots. When piloting an AT-ST in battle, adds 1 to his forfeit, draws one battle destiny if not able to otherwise and cumulatively adds 1 to your attrition against opponent.");
        addIcons(Icon.ENDOR, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingATSTInBattle = new AndCondition(new InBattleCondition(self), new PilotingCondition(self, Filters.AT_ST));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.combat_vehicle));
        modifiers.add(new ForfeitModifier(self, pilotingATSTInBattle, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingATSTInBattle, 1));
        modifiers.add(new AttritionModifier(self, pilotingATSTInBattle, 1, game.getOpponent(self.getOwner()), true));
        return modifiers;
    }
}
