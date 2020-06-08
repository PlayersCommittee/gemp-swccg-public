package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard Walker
 */
public class Card3_157 extends AbstractCombatVehicle {
    public Card3_157() {
        super(Side.DARK, 1, 6, 6, 7, null, 1, 6, "Blizzard Walker", Uniqueness.RESTRICTED_3);
        setLore("Enclosed All Terrain Armored Transport. Commonly called an Imperial walker. One of the most terrifying and deadly weapons in the Empire's arsenal. 15.5 meters tall.");
        setGameText("May add 2 pilots and 8 passengers. Immune to attrition < 4. Landspeed may not be increased.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(2);
        setPassengerCapacity(8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
