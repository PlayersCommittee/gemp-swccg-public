package com.gempukku.swccgo.cards.set1.light;

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
 * Title: Tagge Seeker
 */
public class Card1_160 extends AbstractSeeker {
    public Card1_160() {
        super(Side.LIGHT, 3, "Tagge Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Military version of a 'remote.' Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Deploys for 1 Force to an unoccupied site. Deploys and moves like an undercover spy. When present with Tagge (or warrior) of ability < 3, choose one to be immediately lost (treat as an 'all cards' situation). Seeker is also lost.");
    }

    @Override
    protected Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.or(Filters.Tagge, Filters.warrior), Filters.abilityLessThan(3));
    }
}
