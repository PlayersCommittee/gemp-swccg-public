package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Scout
 */
public class Card3_016 extends AbstractRebel {
    public Card3_016() {
        super(Side.LIGHT, 2, 2, 2, 1, 3, "Rebel Scout", Uniqueness.RESTRICTED_3, ExpansionSet.HOTH, Rarity.C1);
        setLore("A Rebel Scout such as Vidar Blin is usually assigned to recon missions. Trained in first-response tactics, many come to the Alliance with prior special forces experience.");
        setGameText("May move as a 'react' (for free) to a battle where you have a Rebel of ability > 2 or a leader.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationForFreeModifier(self, new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self),
                Filters.or(Filters.and(Filters.Rebel, Filters.abilityMoreThan(2)), Filters.leader))), Filters.battleLocation));
        return modifiers;
    }
}
