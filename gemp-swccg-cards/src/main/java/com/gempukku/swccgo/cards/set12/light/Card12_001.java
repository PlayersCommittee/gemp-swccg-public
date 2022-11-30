package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
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
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Captain Madakor
 */
public class Card12_001 extends AbstractRepublic {
    public Card12_001() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Madakor, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Captain of the Radiant VII. Has gained her rank through consistent excellent performance, and has one of the finest service records of all Republic officers.");
        setGameText("Adds 2 to power of anything she pilots. While piloting Radiant VII and you have no other starships at same system, opponent may not draw more than one battle destiny here.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.Radiant_VII);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self),
                new AndCondition(new PilotingAtCondition(self, Filters.Radiant_VII, Filters.system),
                new CantSpotCondition(self, Filters.and(Filters.your(self), Filters.starship, Filters.not(Filters.Radiant_VII),
                        Filters.atSameSystem(self)))), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
