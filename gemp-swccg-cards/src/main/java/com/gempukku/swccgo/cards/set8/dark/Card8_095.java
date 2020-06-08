package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.InLocalTroubleBattleCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Avarik
 */
public class Card8_095 extends AbstractImperial {
    public Card8_095() {
        super(Side.DARK, 3, 2, 2, 1, 3, Title.Avarik, Uniqueness.UNIQUE);
        setLore("Frequently engaged in brawls at the local enlisted clubs on homeworld of Corulag. Stormtrooper assigned to biker scout unit. Monitors Yuzzum activity.");
        setGameText("Adds 3 to power of any speeder bike he pilots (when piloting it in battle, also adds 1 to your total battle destiny). When in a Local Trouble battle, adds one destiny to your total power.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.speeder_bike));
        modifiers.add(new TotalBattleDestinyModifier(self, new AndCondition(new PilotingCondition(self, Filters.speeder_bike),
                new InBattleCondition(self)), 1, self.getOwner()));
        modifiers.add(new AddsDestinyToPowerModifier(self, new InLocalTroubleBattleCondition(self), 1));
        return modifiers;
    }
}
