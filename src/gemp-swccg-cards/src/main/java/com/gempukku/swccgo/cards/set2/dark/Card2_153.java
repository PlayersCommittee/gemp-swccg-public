package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Squadron
 * Title: TIE Assault Squadron
 */
public class Card2_153 extends AbstractSquadron {
    public Card2_153() {
        super(Side.DARK, 3, null, 3, null, 3, null, 6, Title.TIE_Assault_Squadron, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Imperial strategy relies on quantity over quality. Although many of their pilots have low motivation, this is compensated by 'wolfpack' group tactics.");
        setGameText("* Replaces 3 TIE/lns at one location (TIE/lns go to Used Pile). Permanent pilots provide total ability of 3.");
        addIcons(Icon.A_NEW_HOPE);
        addIcon(Icon.PILOT, 3);
        addModelTypes(ModelType.TIE_LN, ModelType.TIE_LN, ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE);
        setReplacementForSquadron(3, Filters.TIE_ln);
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
