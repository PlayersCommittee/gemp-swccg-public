package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
import com.gempukku.swccgo.cards.conditions.TotalAbilityPilotingMoreThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Lieutenant Williams
 */
public class Card12_012 extends AbstractRepublic {
    public Card12_012() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Williams, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Pilot of the Radiant VII. Was personally requested by Captain Madakor to assist in the transportation of the Jedi ambassadors.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Radiant VII and you have no other starships at same system, unless opponent has total ability > 6 piloting here, opponent's total battle destiny here is -3.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Radiant_VII);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self),
                new AndCondition(new PilotingAtCondition(self, Filters.Radiant_VII, Filters.system),
                        new CantSpotCondition(self, Filters.and(Filters.your(self), Filters.starship, Filters.not(Filters.Radiant_VII), Filters.atSameSystem(self))),
                        new UnlessCondition(new TotalAbilityPilotingMoreThanCondition(opponent, 6, Filters.here(self)))),
                -3, opponent));
        return modifiers;
    }
}
