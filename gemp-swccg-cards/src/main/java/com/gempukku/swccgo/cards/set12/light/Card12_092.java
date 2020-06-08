package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Starfighter
 * Title: Radiant VII
 */
public class Card12_092 extends AbstractStarfighter {
    public Card12_092() {
        super(Side.LIGHT, 2, 4, 5, 4, null, 4, 7, "Radiant VII", Uniqueness.UNIQUE);
        setLore("Optimized for diplomatic missions with sensor-proof pods that have ejection capabilities. Easily identified by its red coloration.");
        setGameText("May add 3 pilots and 3 passengers. Has ship-docking capability. While Madakor or Williams piloting, immune to Lateral Damage and attrition < 5.");
        addPersona(Persona.RADIANT_VII);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.CORELLIAN_REPUBLIC_CRUISER);
        setPilotCapacity(3);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.or(Filters.Madakor, Filters.Williams));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition makadorOrWilliamsPiloting = new HasPilotingCondition(self, Filters.or(Filters.Madakor, Filters.Williams));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, makadorOrWilliamsPiloting, Title.Lateral_Damage));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, makadorOrWilliamsPiloting, 5));
        return modifiers;
    }
}
