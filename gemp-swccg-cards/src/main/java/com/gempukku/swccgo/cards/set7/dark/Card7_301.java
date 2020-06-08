package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Capital
 * Title: Bossk In Hound's Tooth
 */
public class Card7_301 extends AbstractCapitalStarship {
    public Card7_301() {
        super(Side.DARK, 1, 8, 5, 4, null, 4, 5, "Bossk In Hound's Tooth", Uniqueness.UNIQUE);
        setLore("Bossk once said of his ship, 'Greeezeg uut nikek!'");
        setGameText("May add 6 passengers and 1 vehicle. Permanent pilot is •Bossk, who provides ability of 2, adds 2 to power and adds one battle destiny. Deploys and moves like a starfighter. Has ship-docking capability.");
        addPersonas(Persona.HOUNDS_TOOTH);
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_CORELLIAN_FREIGHTER);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.BOSSK, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        modifiers.add(new AddsBattleDestinyModifier(self, 1));
                        return modifiers;
                    }});
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }
}
