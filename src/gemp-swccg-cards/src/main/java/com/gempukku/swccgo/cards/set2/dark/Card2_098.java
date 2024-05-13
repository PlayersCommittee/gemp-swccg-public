package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Officer Evax
 */
public class Card2_098 extends AbstractImperial {
    public Card2_098() {
        super(Side.DARK, 2, 2, 2, 2, 5, "Officer Evax", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.C1);
        setLore("This Imperial Intelligence leader has a proven track record for predicting Rebel fleet movements. His coordination of starship maneuvers has saved many vulnerable bases.");
        setGameText("Adds 2 to power of anything he pilots. When at a Death Star site, Imperial starships may move to the Death Star system as a 'react.'");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move an Imperial starship as a 'react'",
                new AtCondition(self, Filters.Death_Star_site), self.getOwner(), Filters.Imperial_starship, Filters.Death_Star_system));
        return modifiers;
    }
}
