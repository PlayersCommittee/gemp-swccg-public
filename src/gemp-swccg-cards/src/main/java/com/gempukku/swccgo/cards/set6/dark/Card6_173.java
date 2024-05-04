package com.gempukku.swccgo.cards.set6.dark;

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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Vehicle
 * Subtype: Transport
 * Title: Skiff
 */
public class Card6_173 extends AbstractTransportVehicle {
    public Card6_173() {
        super(Side.DARK, 2, 3, 3, null, 3, 3, 3, "Skiff", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Repulsorlift engine. 9.2 meters long. controlled by two directional steering vanes. Jabba's fleet of skiffs is maintained by Barada.");
        setGameText("Deploy -1 to a Tatooine site. May add 1 driver and 5 passengers. May move as a 'react.' If lost, any characters aboard may 'jump off' (disembark).");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.SKIFF);
        setDriverCapacity(1);
        setPassengerCapacity(5);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Tatooine_site));
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
