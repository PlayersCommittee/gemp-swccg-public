package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.MayMoveCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Guard
 */
public class Card1_026 extends AbstractRebel {
    public Card1_026() {
        super(Side.LIGHT, 3, 2, 0, 1, 1, Title.Rebel_Guard, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Galen Torg was selected for honor guard duty during ceremony following the Battle of Yavin. He is typical of the hard-working, unsung heros in the Rebel Alliance.");
        setGameText("Power +4 when defending a battle. Cannot move.");
        addKeyword(Keyword.GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 4));
        modifiers.add(new MayNotMoveModifier(self, new NotCondition(new MayMoveCondition(self))));
        return modifiers;
    }
}
