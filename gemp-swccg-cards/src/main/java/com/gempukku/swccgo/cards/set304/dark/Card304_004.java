package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Elaine Conrat
 */
public class Card304_004 extends AbstractImperial {
    public Card304_004() {
        super(Side.DARK, 1, 3, 3, 3, 4, "Elaine Conrat", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Born on Raxus Sencundus she and her brother grew up under an abusive father. She is fiercely competitive with her brother, Ellac, and strives to overtake him.");
        setGameText("Power +1 when with Ellac. May draw one battle Destiny if otherwise unable to.");
        addIcons(Icon.WARRIOR, Icon.CSP);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.Ellac), 1));
        return modifiers;
    }
		
}
