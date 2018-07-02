package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Boba Fett In Slave I (V)
 */
public class Card200_130 extends AbstractStarfighter {
    public Card200_130() {
        super(Side.DARK, 1, 6, 7, null, 6, 4, 7, "Boba Fett In Slave I", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Dangerous and deadly starfighter piloted by its notorious owner. Uses combat-grade shields and sensors. Hidden weapons provide lethal surprises for Fett's victims.");
        setGameText("Permanent pilot is •Boba Fett, who provides ability of 3. Draws one battle destiny if unable to otherwise. Slave I's power may not be reduced by opponent. Immune to Eject! Eject! and attrition < 5.");
        addPersonas(Persona.SLAVE_I);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.BOBA_FETT, 3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new MayNotHavePowerReducedModifier(self, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Eject_Eject));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
