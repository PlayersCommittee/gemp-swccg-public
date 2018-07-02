package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DifferentCardTitlesParticipatingInBattleCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Effect
 * Title: Hutt Influence
 */
public class Card112_011 extends AbstractNormalEffect {
    public Card112_011() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Hutt_Influence, Uniqueness.UNIQUE);
        setLore("Jabba's criminal empire extends to all reaches of the Outer Rim.");
        setGameText("Deploy on table. Opponent may not cancel or modify Force drains at each Tatooine battleground site where you have two aliens with different card titles. Also, attrition against you is reduced by 2 when two of your non-unique aliens with different card titles are in battle.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter tatooineBattlegroundSiteWithAliensWithDiffCardTitles = Filters.and(Filters.Tatooine_site, Filters.battleground_site,
                Filters.hasDifferentCardTitlesAtLocation(self, Filters.and(Filters.your(self), Filters.alien)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, tatooineBattlegroundSiteWithAliensWithDiffCardTitles, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, tatooineBattlegroundSiteWithAliensWithDiffCardTitles, opponent, playerId));
        modifiers.add(new AttritionModifier(self, new DifferentCardTitlesParticipatingInBattleCondition(Filters.and(Filters.your(self),
                Filters.non_unique, Filters.alien)), -2, playerId));
        return modifiers;
    }
}