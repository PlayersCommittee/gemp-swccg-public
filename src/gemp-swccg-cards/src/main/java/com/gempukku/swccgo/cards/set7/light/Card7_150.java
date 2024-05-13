package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Squadron
 * Title: X-wing Assault Squadron
 */
public class Card7_150 extends AbstractSquadron {
    public Card7_150() {
        super(Side.LIGHT, 2, null, 9, null, 4, 5, 12, "X-wing Assault Squadron", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("A squadron of X-wings ran interference for Y-wings during their assault on the Death Star. Keeping a tight formation dramatically increased the squadron's efficiency.");
        setGameText("* Replaces 3 X-wings at one location (X-wings go to Used Pile). Permanent pilots provide total ability of 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addIcon(Icon.PILOT, 3);
        addIcon(Icon.NAV_COMPUTER, 3);
        addIcon(Icon.SCOMP_LINK, 3);
        addModelTypes(ModelType.X_WING, ModelType.X_WING, ModelType.X_WING);
        setReplacementForSquadron(3, Filters.X_wing);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }
}
