package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CharactersAboardMayJumpOffModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithPilotOrDriverAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Vehicle
 * Subtype: Transport
 * Title: Racing Skiff
 */
public class Card112_018 extends AbstractTransportVehicle {
    public Card112_018() {
        super(Side.DARK, 4, 2, 1, null, 4, 3, 3, "Racing Skiff", Uniqueness.UNRESTRICTED, ExpansionSet.JPSD, Rarity.PM);
        setLore("Barada and Klaatu strip down and rebuild old skiffs to race on the Dune Sea. Jabba allows the practice to continue for now.");
        setGameText("May deploy with a driver (must be Barada, Klaatu, or a non-unique alien) as a 'react'. May add 1 driver and 3 passengers. May move as a 'react'. If lost, any characters aboard may 'jump off' (disembark).");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.SKIFF);
        setDriverCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithPilotOrDriverAsReactModifier(self, Filters.or(Filters.Barada, Filters.Klaatu, Filters.and(Filters.non_unique, Filters.alien))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CharactersAboardMayJumpOffModifier(self));
        return modifiers;
    }
}
