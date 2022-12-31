package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Vader's Personal Shuttle (V)
 */
public class Card200_138 extends AbstractStarfighter {
    public Card200_138() {
        super(Side.DARK, 3, 2, 2, null, 3, 5, 4, "Vader's Personal Shuttle", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Customized transport of Lord Vader. Employs advanced sensor jamming gear. Modified with enhanced tactical displays constructed to the Dark Lord's specifications.");
        setGameText("May add 2 pilots and 4 passengers. Vader deploys -3 aboard. While Vader piloting at a battleground, maneuver +2 and you lose no Force to Uncontrollable Fury or You Must Confront Vader.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        addModelTypes(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setMatchingPilotFilter(Filters.Vader);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Vader, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Vader, -3, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition vaderPilotingAtBattleground = new AndCondition(new HasPilotingCondition(self, Filters.Vader), new AtCondition(self, Filters.battleground));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, vaderPilotingAtBattleground, 2));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.or(Filters.Uncontrollable_Fury, Filters.You_Must_Confront_Vader), vaderPilotingAtBattleground, playerId));
        return modifiers;
    }
}
