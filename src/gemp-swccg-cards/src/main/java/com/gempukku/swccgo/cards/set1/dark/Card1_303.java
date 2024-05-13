package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Advanced x1
 */
public class Card1_303 extends AbstractStarfighter {
    public Card1_303() {
        super(Side.DARK, 3, 2, 2, null, 2, null, 3, "TIE Advanced x1", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("TIE advanced x1 fighter boasting improved power plant, stronger shields, armored hull and enhanced weapons. Deployed to elite Imperial Navy pilots.");
        setGameText("Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.PILOT);
        addModelType(ModelType.TIE_ADVANCED_X1);
        addKeywords(Keyword.NO_HYPERDRIVE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
