package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Mon Calamari Admiral
 */
public class Card203_009 extends AbstractRebel {
    public Card203_009() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, "Mon Calamari Admiral", Uniqueness.RESTRICTED_2, ExpansionSet.SET_3, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 2: any capital starship. Your Rebel starships may move to here for free. While piloting a Rebel capital starship, opponent may not modify or cancel your battle destinies here.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter here = Filters.here(self);
        Condition pilotingRebelCapitalStarship = new PilotingCondition(self, Filters.Rebel_capital_starship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.capital_starship));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.your(self), Filters.Rebel_starship), here));
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, here, playerId, pilotingRebelCapitalStarship, opponent));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, here, playerId, pilotingRebelCapitalStarship, opponent));
        return modifiers;
    }
}
