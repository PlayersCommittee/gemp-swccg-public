package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Cabbel
 */
public class Card3_089 extends AbstractImperial {
    public Card3_089() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Lieutenant Cabbel", Uniqueness.UNIQUE);
        setLore("A recent graduate of the Officer's Candidate School on Carida. Serves as first officer of the Tyrant. Ambitious, ruthless and efficient.");
        setGameText("Adds 2 to power of anything he pilots. On Tyrant, also adds 1 to armor. When in battle with an Imperial leader, subtracts 1 from opponent's total battle destiny.");
        addIcons(Icon.HOTH, Icon.PILOT);
        setMatchingStarshipFilter(Filters.Tyrant);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ArmorModifier(self, Filters.and(Filters.Tyrant, Filters.hasPiloting(self)), 1));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.Imperial_leader),
                -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
