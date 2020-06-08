package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Stinger
 */
public class Card10_049 extends AbstractStarfighter {
    public Card10_049() {
        super(Side.DARK, 3, 2, 3, null, 4, 5, 4, "Stinger", Uniqueness.UNIQUE);
        setLore("Constructed by a secretive Surronian hive craftguild. Equipped with H2-1 hyperdrive system and emergency braking jets. Guri's personal starship. Gift from Prince Xizor.");
        setGameText("Permanent pilot provides 1 ability. May add Guri as pilot (suspends permanent pilot). Guri deploys aboard for free. When Guri piloting, adds one battle destiny and immune to attrition < 5.");
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
        return Filters.Guri;
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
        modifiers.add(new DeployForFreeForSimultaneouslyDeployingPilotModifier(self, Filters.Guri));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Guri, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition guriPiloting = new HasPilotingCondition(self, Filters.Guri);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, guriPiloting, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, guriPiloting, 5));
        return modifiers;
    }
}
