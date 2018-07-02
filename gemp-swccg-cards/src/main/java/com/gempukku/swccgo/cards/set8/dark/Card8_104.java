package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Watts
 */
public class Card8_104 extends AbstractImperial {
    public Card8_104() {
        super(Side.DARK, 2, 2, 2, 2, 4, Title.Watts, Uniqueness.UNIQUE);
        setLore("Native of Corulag. Watts' gunnery skills produce devastating results. Temporarily assigned to Kuat Drive Yards to work on prototype AT-ST weaponry.");
        setGameText("Adds 2 to power of any combat vehicle he pilots. Adds 1 to each weapon destiny draw of any combat vehicle he pilots (or 2 if on Tempest Scout 2 or if present with Marquand).");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        setMatchingVehicleFilter(Filters.Tempest_Scout_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.combat_vehicle));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.and(Filters.combat_vehicle, Filters.hasPiloting(self)),
                new ConditionEvaluator(1, 2, new OrCondition(new PilotingCondition(self, Filters.Tempest_Scout_2), new PresentWithCondition(self, Filters.Marquand)))));
        return modifiers;
    }
}
