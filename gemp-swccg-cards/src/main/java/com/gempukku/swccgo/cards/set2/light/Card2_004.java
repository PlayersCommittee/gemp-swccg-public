package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Evram Lajaie
 */
public class Card2_004 extends AbstractRebel {
    public Card2_004() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, "Commander Evram Lajaie", Uniqueness.UNIQUE);
        setLore("Popular leader whose expertise in space defense and orbital battle stations enabled the Alliance to uncover a fatal flaw in the Death Star's design.");
        setGameText("When at a Yavin 4, Hoth or Endor site, Rebel starships may move to the related system as a 'react'.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a Rebel starship as a 'react'",
                new AtCondition(self, Filters.or(Filters.Yavin_4_site, Filters.Hoth_site, Filters.Endor_site)), self.getOwner(), Filters.Rebel_starship, Filters.relatedSystem(self)));
        return modifiers;
    }
}
