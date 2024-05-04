package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 4
 */
public class Card2_151 extends AbstractStarfighter {
    public Card2_151() {
        super(Side.DARK, 2, 2, 2, null, 3, null, 3, Title.Black_4, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("TIE fighter flown by DS-61-4 at the Battle of Yavin. Scored a hit on Red 5 during the confrontation.");
        setGameText("May add 1 pilot. May deploy with a pilot as a 'react' to a battle initiated against a TIE (for free if TIE is Black 2 or 3).");
        addIcons(Icon.A_NEW_HOPE);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        Condition yourTieDefendingBattle = new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.TIE, Filters.defendingBattle));
        Condition yourBlack2or3DefendingBattle = new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.TIE, Filters.or(Filters.Black_2, Filters.Black_3), Filters.defendingBattle));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToBattleModifier(self, new AndCondition(yourTieDefendingBattle, new NotCondition(yourBlack2or3DefendingBattle))));
        modifiers.add(new MayDeployAsReactToBattleForFreeModifier(self, yourBlack2or3DefendingBattle));
        return modifiers;
    }
}
