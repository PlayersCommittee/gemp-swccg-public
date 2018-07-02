package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.EpicEventCalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Major Hewex
 */
public class Card8_105 extends AbstractImperial {
    public Card8_105() {
        super(Side.DARK, 2, 2, 2, 2, 4, Title.Hewex, Uniqueness.UNIQUE);
        setLore("Leader of Navy detachment assigned to guard the Endor control bunker. Liaison between Imperial technicians and command staff.");
        setGameText("Adds 1 to total power for each of your troopers present at same site. When on Endor, subtracts 1 from total for Deactivate The Shield Generator and your troopers are forfeit +1 on Endor. When at Bunker, allows Imperials to deploy there as a 'react'.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onEndor = new OnCondition(self, Title.Endor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self), new AtCondition(self, Filters.site),
                new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.trooper)), self.getOwner()));
        modifiers.add(new EpicEventCalculationTotalModifier(self, Filters.Deactivate_The_Shield_Generator, onEndor, -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.trooper, Filters.on(Title.Endor)), onEndor, 1));

        // TODO: Fix this
        //modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.Imperial, new AtCondition(self, Filters.Bunker), Filters.Bunker));
        return modifiers;
    }
}
