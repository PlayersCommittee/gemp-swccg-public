package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Myn Kyneugh
 */
public class Card9_118 extends AbstractImperial {
    public Card9_118() {
        super(Side.DARK, 4, 3, 4, 3, 5, "Myn Kyneugh", Uniqueness.UNIQUE);
        setLore("Royal guard leader. Remembers nothing of his past other than serving his Emperor. Early instructor of Kir Kanos and Carnor Jax.");
        setGameText("Deploys only on Coruscant or to Emperor's site (or related site). When armed with a Force pike, adds one battle destiny. Your troopers and Royal Guards may 'react' to here for free. Adds 1 to defense value of other Royal Guards at same and related sites.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_GUARD, Keyword.LEADER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Coruscant, Filters.locationAndCardsAtLocation(Filters.sameOrRelatedSiteAs(self, Filters.Emperor)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourTrooperOrRoyalGuard = Filters.and(Filters.your(self), Filters.or(Filters.trooper, Filters.Royal_Guard));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new ArmedWithCondition(self, Filters.Force_pike), 1));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationForFreeModifier(self, "Deploy trooper or Royal Guard as a 'react'",
                playerId, yourTrooperOrRoyalGuard, Filters.here(self)));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationForFreeModifier(self, "Move trooper or Royal Guard as a 'react'",
                playerId, yourTrooperOrRoyalGuard, Filters.here(self)));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.other(self), Filters.Royal_Guard, Filters.atSameOrRelatedSite(self)), 1));
        return modifiers;
    }
}
