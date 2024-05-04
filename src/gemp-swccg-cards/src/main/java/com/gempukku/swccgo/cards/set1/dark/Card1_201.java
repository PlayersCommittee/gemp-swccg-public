package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Comlink
 */
public class Card1_201 extends AbstractCharacterDevice {
    public Card1_201() {
        super(Side.DARK, 3, Title.Comlink, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("Hand-held communication device. Military comlinks have scrambling and variable frequency programming for secure communication during battles or emergencies.");
        setGameText("Use 1 Force to deploy on any character. If opponent has just initiated a battle or Force drain at Comlink's location or an adjacent site, you may 'react' by deploying cards (at normal use of the Force) to that battle or Force drain location.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a card as a 'react'", self.getOwner(),
                Filters.any, Filters.or(Filters.sameLocation(self), Filters.adjacentSite(self))));
        return modifiers;
    }
}