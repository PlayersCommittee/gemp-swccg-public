package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: CZ-3 (Seezee-Three)
 */
public class Card1_006 extends AbstractDroid {
    public Card1_006() {
        super(Side.LIGHT, 3, 1, 1, 4, "CZ-3 (Seezee-Three)", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("CZ comm droid built by Serv-O-Droid. Outdated but still commonly in use. Built-in comlink. Sophisticated scramblers and encryption programming.");
        setGameText("If opponent has just initiated a battle or Force drain at CZ-3's location or an adjacent site, you may 'react' by deploying cards (at normal use of the Force) to that battle or Force drain location.");
        addModelType(ModelType.COMMUNICATIONS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a card as a 'react'", self.getOwner(),
                Filters.any, Filters.or(Filters.sameLocation(self), Filters.adjacentSite(self))));
        return modifiers;
    }
}
