package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSeeker;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;


/**
 * Set: Premiere
 * Type: Weapon
 * Subtype: Automated
 * Title: Luke Seeker
 */
public class Card1_321 extends AbstractSeeker {
    public Card1_321() {
        super(Side.DARK, 3, "Luke Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Military version of a 'remote.' Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Use 1 Force to deploy on opponent's side at any unoccupied site. Moves during your control phase, like a character, at normal use of the Force. When at same location as Luke of ability < 4 or pilot of ability < 3, choose one to be immediately lost. Seeker also lost.");
    }

    @Override
    protected Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.or(Filters.and(Filters.Luke, Filters.abilityLessThan(4)), Filters.and(Filters.pilot, Filters.abilityLessThan(3))));
    }
}
