package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MayBeReplacedByOpponentModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Komilia Lap'lamiz, Exile
 */
public class Card304_009 extends AbstractAlien {
    public Card304_009() {
        super(Side.LIGHT, 3, 3, 3, 2, 5, Title.Komilia_Laplamiz_Exile, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("After having her 'soup' drank by Sykes Jade, Komilia was exiled from Scholae Palatinae territory by her father, Kamjin. Komilia struggles to survive while she seeks to restore her memory and honor.");
        setGameText("Subtracts 1 from power of anything she pilots. Where present, cancels Kamjin's game text.");
        addPersona(Persona.KOMILIA);
		addKeywords(Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CSP);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, -1));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Kamjin,
                Filters.at(Filters.wherePresent(self))), new PresentCondition(self)));
		modifiers.add(new MayBeReplacedByOpponentModifier(self, new PresentAtCondition(self, Filters.site)));
        return modifiers;
    }
}
