package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Saber 4
 */
public class Card9_165 extends AbstractStarfighter {
    public Card9_165() {
        super(Side.DARK, 3, 2, 3, null, 4, null, 3, "Saber 4", Uniqueness.UNIQUE);
        setLore("TIE interceptor often assigned to fly in a reserve position during battle. The bloodstripe of the 181st denotes the 10 kills scored by DS-181-4.");
        setGameText("May deploy with a pilot as a 'react' to a battle initiated against a TIE (for free if TIE is in Saber Squadron). May add 1 pilot. Immune to attrition < 4 when DS-181-4 piloting.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.SABER_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.DS_181_4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        Condition yourTieDefendingBattle = new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.TIE, Filters.defendingBattle));
        Condition yourSaberSquadronTieDefendingBattle = new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.TIE, Filters.Saber_Squadron, Filters.defendingBattle));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToBattleModifier(self, new AndCondition(yourTieDefendingBattle, new NotCondition(yourSaberSquadronTieDefendingBattle))));
        modifiers.add(new MayDeployAsReactToBattleForFreeModifier(self, yourSaberSquadronTieDefendingBattle));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.DS_181_4), 4));
        return modifiers;
    }
}
