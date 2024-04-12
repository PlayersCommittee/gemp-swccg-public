package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: PD-209
 */
public class Card304_024 extends AbstractImperial {
    public Card304_024() {
        super(Side.DARK, 4, 3, 4, 3, 5, "PD-209", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Scholae Palatinae Royal Guard leader, Remembers nothing of his past other than serving his Emperor. He's dedicated a lifetime to Scholae Palatinae and training other Royal Guards.");
        setGameText("Deploys only on Seraph or to CSP Emperor's site (or related site). When armed with a Force pike adds one battle destiny. Your troopers and Royal Guards may 'react' to here for free. Adds 1 to defense value of other Scholae Palatinae, Royal Guards at same and related sites.");
        addIcons(Icon.CSP, Icon.WARRIOR);
        addKeywords(Keyword.CSP_ROYAL_GUARD, Keyword.LEADER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Seraph, Filters.locationAndCardsAtLocation(Filters.sameOrRelatedSiteAs(self, Filters.CSP_EMPEROR)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourTrooperOrRoyalGuard = Filters.and(Filters.your(self), Filters.or(Filters.trooper, Filters.CSP_ROYAL_GUARD));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new ArmedWithCondition(self, Filters.Force_pike), 1));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationForFreeModifier(self, "Deploy trooper or CSP Royal Guard as a 'react'",
                playerId, yourTrooperOrRoyalGuard, Filters.here(self)));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationForFreeModifier(self, "Move trooper or CSP Royal Guard as a 'react'",
                playerId, yourTrooperOrRoyalGuard, Filters.here(self)));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.other(self), Filters.CSP_ROYAL_GUARD, Filters.atSameOrRelatedSite(self)), 1));
        return modifiers;
    }
}
