package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: Block 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tantive IV (V)
 */
public class Card601_144 extends AbstractCapitalStarship {
    public Card601_144() {
        super(Side.LIGHT, 1, 4, 5, 4, null, 3, 7, Title.Tantive_IV, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Royal House of Alderaan consular ship. Used by Princess Leia for Imperial Senate business (and secret Rebel espionage). Captured by the Devastator over Tatooine.");
        setGameText("May deploy without presence or Force icons. May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Adds one [Light Side Force] and one [Dark Side Force] at same Dark Side system.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_1);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        addPersona(Persona.TANTIVE_IV);
        setPilotCapacity(3);
        setPassengerCapacity(4);
        setAsLegacy(true);
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
        modifiers.add(new IconModifier(self, Filters.and(Filters.sameLocation(self), Filters.opponents(self), Filters.system), Icon.LIGHT_FORCE, 1));
        modifiers.add(new IconModifier(self, Filters.and(Filters.sameLocation(self), Filters.opponents(self), Filters.system), Icon.DARK_FORCE, 1));
        return modifiers;
    }
}
