package com.gempukku.swccgo.cards.set221.light;

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
 * Set: Set 21
 * Type: Starship
 * Subtype: Capital
 * Title: Endurance
 */
public class Card221_058 extends AbstractCapitalStarship {
    public Card221_058() {
        super(Side.LIGHT, 2, 6, 7, 6, null, 4, 7, "Endurance", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("May add 5 pilots, 5 passengers, 5 vehicles, and 5 starfighters. Permanent pilot provides ability of 2. Kilian, Mace, and clones deploy -1 aboard and shuttle from here for free. Immune to attrition < 3.");
        addModelType(ModelType.VENATOR_CLASS_ATTACK_CRUISER);
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.CLONE_ARMY, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_21);
        setPilotCapacity(5);
        setPassengerCapacity(5);
        setVehicleCapacity(5);
        setStarfighterCapacity(5);
        setMatchingPilotFilter(Filters.or(Filters.title("Admiral Kilian"), Filters.Mace));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.or(Filters.title("Admiral Kilian"), Filters.Mace, Filters.clone), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostAboardModifier(self, Filters.or(Filters.title("Admiral Kilian"), Filters.Mace, Filters.clone),  -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, Filters.or(Filters.title("Admiral Kilian"), Filters.Mace, Filters.clone), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}