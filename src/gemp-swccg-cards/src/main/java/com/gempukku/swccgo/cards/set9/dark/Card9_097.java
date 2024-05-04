package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Chiraneau
 */
public class Card9_097 extends AbstractImperial {
    public Card9_097() {
        super(Side.DARK, 2, 3, 2, 2, 4, Title.Chiraneau, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Deep space transmissions expert. Piett's personal advisor. TIE ace promoted upon Piett's request.");
        setGameText("Adds 3 to power of anything he pilots. When piloting a Star Destroyer at a battleground system, adds 1 to your Force drains here and at each other battleground system controlled by a Star Destroyer within 2 parsecs of Chiraneau.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ADMIRAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ForceDrainModifier(self, Filters.or(Filters.here(self), Filters.and(Filters.battleground_system,
                Filters.controlsWith(playerId, self, Filters.Star_Destroyer), Filters.withinParsecsOf(self, 2))),
                new AndCondition(new PilotingCondition(self, Filters.Star_Destroyer), new AtCondition(self, Filters.battleground_system)),
                1, playerId));
        return modifiers;
    }
}
