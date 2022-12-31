package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Boba Fett In Slave I
 */
public class Card109_008 extends AbstractStarfighter {
    public Card109_008() {
        super(Side.DARK, 1, 7, 4, null, 4, 4, 4, "Boba Fett In Slave I", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setLore("Dangerous and deadly starfighter piloted by its notorious owner. Uses combat-grade shields and sensors. Hidden weapons provide lethal surprises for Fett's victims.");
        setGameText("May add 3 passengers. Permanent pilot is •Boba Fett, who provides ability of 3, adds 3 to power, adds 2 to maneuver and draws one battle destiny if not able to otherwise. Immune to attrition < 5.");
        addPersonas(Persona.SLAVE_I);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
        setPassengerCapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.BOBA_FETT, 3) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 3));
                        modifiers.add(new ManeuverModifier(self, 2));
                        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
                        return modifiers;
                    }
                });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
