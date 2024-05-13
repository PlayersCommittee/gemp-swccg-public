package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Starship
 * Subtype: Capital
 * Title: Devastator (V)
 */
public class Card216_008 extends AbstractCapitalStarship {
    public Card216_008() {
        super(Side.DARK, 1, 8, 9, 6, null, 3, 9, Title.Devastator, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Supported the devastating subjugation of Ralltiir. While under the command of Darth Vader, chased and captured the traitor Princess Leia Organa aboard the consular ship Tantive IV.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Permanent pilot provides ability of 2. Vader deploys -2 aboard. May deploy -4 as a 'react' to Scarif or same location as Tantive IV.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_16);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
        setMatchingPilotFilter(Filters.Vader);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Vader, -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter locationFilter = Filters.or(Filters.Scarif_system, Filters.sameLocationAs(self, Filters.Tantive_IV));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, locationFilter, -4));
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Vader, -2));
        return modifiers;
    }
}