package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Dyer
 */
public class Card8_093 extends AbstractImperial {
    public Card8_093() {
        super(Side.DARK, 2, 3, 3, 3, 4, Title.Dyer, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Responsible for defense of control bunker. Leader. Worked closely with Moff Jerjerrod to plan the installation's defense. Instructed to hold troops in reserve.");
        setGameText("While at Bunker or an Endor battleground site, prevents Ominous Rumors from being canceled. Your [Endor] troopers may move as a 'react' to same site.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Ominous_Rumors, new AtCondition(self, Filters.or(Filters.Bunker,
                Filters.and(Filters.Endor_site, Filters.battleground_site)))));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move [Endor] trooper as a 'react'",
                self.getOwner(), Filters.and(Filters.your(self), Icon.ENDOR, Filters.trooper), Filters.sameSite(self)));
        return modifiers;
    }
}
