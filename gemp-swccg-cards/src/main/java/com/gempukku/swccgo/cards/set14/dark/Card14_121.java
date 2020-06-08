package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.LandspeedMayNotBeIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Combat
 * Title: Armored Attack Tank
 */
public class Card14_121 extends AbstractCombatVehicle {
    public Card14_121() {
        super(Side.DARK, 2, 4, 5, 4, null, 1, 5, "Armored Attack Tank", Uniqueness.RESTRICTED_3);
        setLore("The AAT is the most powerful vehicle in the Trade Federation arsenal. It includes range-enhanced laser cannons, projectile launchers and anti-personnel blasters. Enclosed.");
        setGameText("May add 1 pilot and 3 passengers. While piloted at same site as a battle droid, adds two battle destiny. Landspeed may not be increased. Immune to attrition < 4.");
        addModelType(ModelType.AAT);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotedCondition(self),
                new AtSameSiteAsCondition(self, Filters.battle_droid)), 2));
        modifiers.add(new LandspeedMayNotBeIncreasedModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
