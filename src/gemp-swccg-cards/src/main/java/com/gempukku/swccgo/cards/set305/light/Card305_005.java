package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A Better Tomorrow
 * Type: Starship
 * Subtype: Capital
 * Title: Sunrider
 */
public class Card305_005 extends AbstractCapitalStarship {
    public Card305_005() {
        super(Side.LIGHT, 2, 6, 7, 6, null, 4, 7, "Sunrider", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.U);
        setGameText("May add 5 pilots, 5 passengers, 5 vehicles, and 5 starfighters. Permanent pilot provides ability of 2. [COU] with ability < 4 deploy -1 aboard and shuttle from here for free. Immune to attrition < 3.");
        addModelType(ModelType.VENATOR_CLASS_ATTACK_CRUISER);
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.COU, Icon.SCOMP_LINK);
        setPilotCapacity(5);
        setPassengerCapacity(5);
        setVehicleCapacity(5);
        setStarfighterCapacity(5);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.and(Filters.COU_character, Filters.abilityLessThan(4)), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostAboardModifier(self, Filters.and(Filters.COU_character, Filters.abilityLessThan(4)),  -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, Filters.and(Filters.COU_character, Filters.abilityLessThan(4)), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}