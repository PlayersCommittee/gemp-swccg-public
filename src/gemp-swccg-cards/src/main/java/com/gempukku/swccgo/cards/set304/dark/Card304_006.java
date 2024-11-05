package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Kadrol Hauen
 */
public class Card304_006 extends AbstractImperial {
    public Card304_006() {
        super(Side.DARK, 1, 4, 4, 4, 4, "Kadrol Hauen", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Kadrol Hauen is a Pantoran-Human hybrid born in 21 ABY. He's served in both Clan Plagueis and Scholae Palatinae. He was the first member knighted in Caelestis City.");
        setGameText("Adds 2 to any ship he pilots. All droids may be battled.");
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
		addKeywords(Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeBattledModifier(self, Filters.droid));
		modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

}
