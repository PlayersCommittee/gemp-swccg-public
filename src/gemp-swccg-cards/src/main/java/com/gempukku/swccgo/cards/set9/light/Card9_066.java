package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireAnyNumberOfWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: B-wing Bomber
 */
public class Card9_066 extends AbstractStarfighter {
    public Card9_066() {
        super(Side.LIGHT, 3, 3, 4, null, 2, 3, 4, "B-wing Bomber", Uniqueness.UNRESTRICTED, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Carries weapon systems capable of taking on escort frigates and capital starships. Armed with high-powered ion cannon used to disable deflector shields.");
        setGameText("May add 1 pilot (suspends permanent pilot). Permanent pilot provides ability of 1. May fire two or more weapons during battle. Each of its ion cannon weapon destiny draws is +3.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.B_WING);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireAnyNumberOfWeaponsModifier(self, new DuringBattleCondition()));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 3, Filters.ion_cannon));
        return modifiers;
    }
}
