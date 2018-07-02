package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
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
 * Title: Skiff
 */
public class Card112_006 extends AbstractTransportVehicle {
    public Card112_006() {
        super(Side.LIGHT, 4, 2, 1, null, 4, 3, 3, "Racing Skiff");
        setLore("Repulsor lift skiffs are used by traders and merchants to safely cross the deserts of Tatooine. Young thrill seekers sometimes upgrade them with high-powered engines.");
        setGameText("May deploy with a non-unique alien driver as a 'react'. May add 1 driver and 3 passengers. May move as a 'react'. If lost, any characters aboard may 'jump off' (disembark).");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.SKIFF);
        setDriverCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithPilotOrDriverAsReactModifier(self, Filters.and(Filters.non_unique, Filters.alien)));
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
