package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Squadron
 * Title: B-wing Attack Squadron
 */
public class Card9_065 extends AbstractSquadron {
    public Card9_065() {
        super(Side.LIGHT, 3, null, 12, null, 2, 3, 12, "B-wing Attack Squadron");
        setLore("Utilizes dense formations on attack mission to concentrate firepower. This tactic is particularly effective in defeating deflector shields.");
        setGameText("* Replaces 3 B-Wings at one location (B-wings go to Used Pile). Permanent pilots provide total ability of 3. Each of its weapon destiny draws is +1.");
        addIcons(Icon.DEATH_STAR_II);
        addIcon(Icon.PILOT, 3);
        addIcon(Icon.NAV_COMPUTER, 3);
        addIcon(Icon.SCOMP_LINK, 3);
        addModelTypes(ModelType.B_WING, ModelType.B_WING, ModelType.B_WING);
        setReplacementForSquadron(3, Filters.B_wing);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1));
        return modifiers;
    }
}
