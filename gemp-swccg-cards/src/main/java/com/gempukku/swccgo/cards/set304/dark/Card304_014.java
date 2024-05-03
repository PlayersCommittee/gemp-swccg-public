package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Cap
 */
public class Card304_014 extends AbstractAlien {
    public Card304_014() {
        super(Side.DARK, 3, 3, 2, 2, 3, "Cap", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("A former member of the Scholae Palatinae Legion, Cap v");
        setGameText("Thran's Personal Guard members are deploy -1 to same site. Thran's Personal Guard members may move to same Seraph site as a 'react.'");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.THRAN_GUARD, Keyword.LEADER, Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.THRAN_GUARD, -1, Filters.sameSite(self)));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move Thran's Guardsmen as a 'react'", new AtCondition(self, Filters.Seraph_site),
                self.getOwner(), Filters.and(Filters.your(self), Filters.THRAN_GUARD), Filters.sameSite(self)));
        return modifiers;
    }
}
