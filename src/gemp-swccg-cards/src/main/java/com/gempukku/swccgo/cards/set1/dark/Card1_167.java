package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Praji
 */
public class Card1_167 extends AbstractImperial {
    public Card1_167() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Commander Praji", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Vader's aide on the Devastator. Personally supervised search for Death Star plans on Tatooine by Vader's order. Was graduated with honors from Imperial Navy Academy on Carida.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Devastator, also adds 1 to hyperspeed. Where present, cancels game text of C-3PO or R2-D2.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER);
        setMatchingStarshipFilter(Filters.Devastator);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.Devastator, Filters.hasPiloting(self)), 1));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.or(Filters.C3PO, Filters.R2D2),
                Filters.at(Filters.wherePresent(self))), new PresentCondition(self)));
        return modifiers;
    }
}
