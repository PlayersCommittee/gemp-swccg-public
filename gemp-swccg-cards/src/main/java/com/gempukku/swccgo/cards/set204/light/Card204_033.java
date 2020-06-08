package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Starship
 * Subtype: Capital
 * Title: Masanya (V)
 */
public class Card204_033 extends AbstractCapitalStarship {
    public Card204_033() {
        super(Side.LIGHT, 2, 4, 5, 4, null, 3, 8, "Masanya", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Frequently escorts Mon Calamari star cruisers. Personally assigned by Ackbar to main Rebel fleet. Advanced scanners continuously disrupt target acquisition signals.");
        setGameText("Deploys -2 to same system as any Star Cruiser. May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Weapon destinies targeting your starships here are -2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_4);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.sameSystemAs(self, Filters.Star_Cruiser)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, -2, Filters.and(Filters.your(self), Filters.starship, Filters.here(self))));
        return modifiers;
    }
}
