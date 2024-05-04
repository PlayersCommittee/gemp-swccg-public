package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.MayMoveCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
 * Subtype: Imperial
 * Title: Imperial Trooper Guard
 */
public class Card1_181 extends AbstractImperial {
    public Card1_181() {
        super(Side.DARK, 3, 2, 0, 1, 1, "Imperial Trooper Guard", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Elite soldiers trained in combat techniques and weapons skills. Death Star trooper Tajis Durmin is typical of those assigned to guard key areas of the Death Star.");
        setGameText("Power +4 when defending a battle. Cannot move.");
        addKeywords(Keyword.IMPERIAL_TROOPER_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 4));
        modifiers.add(new MayNotMoveModifier(self, new NotCondition(new MayMoveCondition(self))));
        return modifiers;
    }
}
