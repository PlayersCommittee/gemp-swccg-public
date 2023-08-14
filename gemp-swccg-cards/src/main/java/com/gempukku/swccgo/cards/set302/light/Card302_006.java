package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Padawan Learner
 */
public class Card302_006 extends AbstractAlien {
    public Card302_006() {
        super(Side.LIGHT, 2, 3, 2, 3, 2, "Padawan Learner", Uniqueness.RESTRICTED_3, ExpansionSet.DJB_CORE, Rarity.C1);
        setLore("Despite rumors whispered for ages, the Brotherhood embraces all aspects oft he Force. Young Jedi have flourished within Brotherhood space without the fear of attack.");
        setGameText("May move as a 'react' (for free) to a battle where you have a Rebel of ability > 2 or a leader.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationForFreeModifier(self, new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self),
                Filters.or(Filters.and(Filters.Rebel, Filters.abilityMoreThan(2)), Filters.leader))), Filters.battleLocation));
        return modifiers;
    }
}
