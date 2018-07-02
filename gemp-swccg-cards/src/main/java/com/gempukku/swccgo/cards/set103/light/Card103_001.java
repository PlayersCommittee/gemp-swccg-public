package com.gempukku.swccgo.cards.set103.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Rebel Leader Pack)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold Leader In Gold 1
 */
public class Card103_001 extends AbstractStarfighter {
    public Card103_001() {
        super(Side.LIGHT, 2, 5, 2, null, 3, 4, 4, "Gold Leader In Gold 1", Uniqueness.UNIQUE);
        setLore("At the Battle of Yavin, Dutch led his squadron of outdated but reliable Y-wings in the first wave of the assault against the Death Star.");
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard is •Dutch, who provides ability of 2, adds 2 to power and may draw one battle destiny if not able to otherwise.");
        addPersonas(Persona.GOLD_1);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.NAV_COMPUTER);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.DUTCH, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
                        return modifiers;
                    }
                });
    }
}
