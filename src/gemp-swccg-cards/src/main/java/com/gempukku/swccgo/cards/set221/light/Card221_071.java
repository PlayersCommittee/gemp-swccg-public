package com.gempukku.swccgo.cards.set221.light;

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
 * Set: Set 21
 * Type: Starship
 * Subtype: Capital
 * Title: Resolute
 */
public class Card221_071 extends AbstractCapitalStarship {
    public Card221_071() {
        super(Side.LIGHT, 2, 6, 7, 6, null, 4, 7, "Resolute", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("May add 5 pilots, 5 passengers, 5 vehicles, and 5 starfighters. Permanent pilot provides ability of 2. While Anakin or Yularen piloting, immune to attrition < 5 and your [Clone Army] cards at related sites are power +1.");
        addModelType(ModelType.VENATOR_CLASS_ATTACK_CRUISER);
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.CLONE_ARMY, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_21);
        setPilotCapacity(5);
        setPassengerCapacity(5);
        setVehicleCapacity(5);
        setStarfighterCapacity(5);
        setMatchingPilotFilter(Filters.or(Filters.Anakin, Filters.Yularen));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Anakin, Filters.Yularen)), 5));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Icon.CLONE_ARMY, Filters.at(Filters.relatedSite(self))), new HasPilotingCondition(self, Filters.or(Filters.Anakin, Filters.Yularen)), 1));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}