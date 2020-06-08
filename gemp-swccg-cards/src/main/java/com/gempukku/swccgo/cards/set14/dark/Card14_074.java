package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: Battle Droid Pilot
 */
public class Card14_074 extends AbstractDroid {
    public Card14_074() {
        super(Side.DARK, 3, 2, 0, 2, "Battle Droid Pilot");
        setArmor(2);
        setLore("Pilot battle droids require navigational programming not found in other types of battle droids. Therefore, even though they lack weaponry, their manufacturing cost is still the same.");
        setGameText("Adds 2 to power of anything he pilots (3 while piloting a battleship). While piloting a battleship, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingBattleship = new PilotingCondition(self, Filters.battleship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 3, pilotingBattleship)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingBattleship, 1));
        return modifiers;
    }
}
