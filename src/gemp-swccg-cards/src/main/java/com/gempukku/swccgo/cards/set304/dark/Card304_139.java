package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Korvyn, Hand Of The Emperor
 */
public class Card304_139 extends AbstractDarkJediMasterImperial {
    public Card304_139() {
        super(Side.DARK, 1, 6, 5, 7, 7, "Korvyn, Hand Of The Emperor", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Korvyn is a leader with Scholae Palatinae. Currently serving as the Hand of the Emperor. Spending his childhood as a gangster he has kept his Force powers hidden to gain an edge over his opponents.");
        setGameText("Adds 2 to power of anything he pilots. When in battle at a site, draws one battle destiny if not able to otherwise and total ability of 6 or more required for opponent to draw battle destiny here. Immune to attrition < 5 when [CSP] Emperor here.");
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER);
        addPersona(Persona.KORVYN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleAtSite = new InBattleAtCondition(self, Filters.site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, inBattleAtSite, 1));
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, Filters.here(self), inBattleAtSite, 6, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HereCondition(self, Filters.CSP_EMPEROR), 5));
        return modifiers;
    }
}
