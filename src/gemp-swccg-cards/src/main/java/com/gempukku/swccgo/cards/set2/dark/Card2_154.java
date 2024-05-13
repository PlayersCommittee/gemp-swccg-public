package com.gempukku.swccgo.cards.set2.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Vanguard
 */
public class Card2_154 extends AbstractStarfighter {
    public Card2_154() {
        super(Side.DARK, 3, 2, 1, null, 2, null, 3, "TIE Vanguard", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Reconnaissance starfighter. Often deployed first to gather detailed information on enemy starship movement before full fleet engagement.");
        setGameText("Permanent pilot provides ability of 1. You may deploy cards to same system or sector as a 'react'.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addModelType(ModelType.TIE_RC);
        addKeywords(Keyword.NO_HYPERDRIVE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a card as a 'react'", self.getOwner(),
                Filters.any, Filters.sameSystemOrSector(self)));
        return modifiers;
    }
}
