package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tantive IV (V)
 */
public class Card201_019 extends AbstractCapitalStarship {
    public Card201_019() {
        super(Side.LIGHT, 1, 4, 5, 4, null, 3, 7, Title.Tantive_IV, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Royal House of Alderaan consular ship. Used by Princess Leia for Imperial Senate business (and secret Rebel espionage). Captured by the Devastator over Tatooine.");
        setGameText("May deploy without presence or Force icons. May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Adds one [Light Side Force] icon here.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_1);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        addPersona(Persona.TANTIVE_IV);
        setPilotCapacity(3);
        setPassengerCapacity(4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithoutPresenceOrForceIconsModifier(self));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }
}
