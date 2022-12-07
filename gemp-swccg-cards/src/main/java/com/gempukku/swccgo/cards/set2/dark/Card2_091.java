package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Commander
 */
public class Card2_091 extends AbstractImperial {
    public Card2_091() {
        super(Side.DARK, 2, 3, 1, 2, 2, "Imperial Commander", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Typical Imperial leader. Uses constant training and crisis simulations to help maintain high performance levels. Commander Daine Jir is known to be bold and outspoken.");
        setGameText("Adds 1 to forfeit of each of your other Imperials (except leaders) at same site.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Imperial, Filters.except(Filters.leader), Filters.atSameSite(self)), 1));
        return modifiers;
    }
}
