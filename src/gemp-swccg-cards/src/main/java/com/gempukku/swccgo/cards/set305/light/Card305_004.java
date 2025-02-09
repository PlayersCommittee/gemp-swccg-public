package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A Better Tomorrow
 * Type: Starship
 * Subtype: Capital
 * Title: Resurgent-II
 */
public class Card305_004 extends AbstractCapitalStarship {
    public Card305_004() {
        super(Side.LIGHT, 2, 6, 7, 6, null, 4, 7, "Resurgent-II", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setGameText("May add 5 pilots, 5 passengers, 5 vehicles, and 5 starfighters. Permanent pilot provides ability of 2. While [COU] piloting with ability > 3, immune to attrition < 5 and your [COU] cards at related sites are power +1.");
        addModelType(ModelType.VENATOR_CLASS_ATTACK_CRUISER);
        addIcons(Icon.COU, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        setPilotCapacity(5);
        setPassengerCapacity(5);
        setVehicleCapacity(5);
        setStarfighterCapacity(5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.and(Filters.COU_character, Filters.abilityMoreThan(3))), 5));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Icon.COU, Filters.at(Filters.relatedSite(self))), new HasPilotingCondition(self, Filters.and(Filters.COU_character, Filters.abilityMoreThan(3))), 1));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}