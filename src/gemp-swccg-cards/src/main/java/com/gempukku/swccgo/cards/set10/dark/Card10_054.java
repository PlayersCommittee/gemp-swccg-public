package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Virago
 */
public class Card10_054 extends AbstractStarfighter {
    public Card10_054() {
        super(Side.DARK, 2, 3, 4, null, 5, 4, 5, "Virago", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Prototype designed by MandalMotors to Prince Xizor's exacting specifications. Contains four separate power generators to power advanced flight controls and engines.");
        setGameText("Permanent Pilot provides 1 ability. May add Xizor as pilot (suspends permanent pilot). Xizor deploys -3 aboard. When Xizor piloting, adds one battle destiny and immune to attrition < 5.");
        addIcons(Icon.REFLECTIONS_II, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.SURRONIAN_CONQUEROR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Guri);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Xizor;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Xizor, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition xizorPiloting = new HasPilotingCondition(self, Filters.Xizor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Xizor, -3, self));
        modifiers.add(new AddsBattleDestinyModifier(self, xizorPiloting, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, xizorPiloting, 5));
        return modifiers;
    }
}
