package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Vehicle
 * Subtype: Transport
 * Title: Sandcrawler
 */
public class Card1_309 extends AbstractTransportVehicle {
    public Card1_309() {
        super(Side.DARK, 2, 3, 3, 3, null, 2, 5, "Sandcrawler", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Warren-like enclosed homes for Jawa tribes. Protect against Tusken Raiders, krayt dragons, etc. 36 meters longs, 17 meters tall. Originally mining vehicles. No two look alike.");
        setGameText("Deploys only to a Tatooine site. May add 1 driver and 7 passengers. Cannot move to mobile sites. Adds 1 to forfeit of each Jawa at same exterior site.");
        addKeywords(Keyword.ENCLOSED, Keyword.SANDCRAWLER);
        setDriverCapacity(1);
        setPassengerCapacity(7);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.locationAndCardsAtLocation(Filters.Tatooine_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.mobile_site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Jawa, Filters.atSameSite(self)),
                new AtCondition(self, Filters.exterior_site), 1));
        return modifiers;
    }
}
