package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: DS-181-3
 */
public class Card9_107 extends AbstractImperial {
    public Card9_107() {
        super(Side.DARK, 3, 1, 1, 2, 2, Title.DS_181_3, Uniqueness.UNIQUE);
        setLore("Flies Saber 3 in fighting 181st. Studied under Baron Fel at the Prefsbelt Imperial Academy; now flies as his wing. Nicknamed 'Fel's Wrath'. Watches the Baron's back.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Saber 3, draws one battle destiny if not able to otherwise. Opponent may not 'react' to same system or sector.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.SABER_SQUADRON);
        setMatchingStarshipFilter(Filters.Saber_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Saber_3), 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSystemOrSector(self), game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
