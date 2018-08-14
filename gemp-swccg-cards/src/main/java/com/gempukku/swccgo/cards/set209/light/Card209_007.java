package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.conditions.Condition;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: General Walex Blissex (V)
 */
public class Card209_007 extends AbstractRebel {
    public Card209_007() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "General Walex Blissex", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Engineer who worked with Jan Dodonna to design the A-wing starfighter. Given honorary rank due to his service to the Rebellion.");
        setGameText("While at a war room, at systems where you have two piloted snub fighters, opponent may draw no more than one battle destiny (you may not cancel those destiny draws) and opponent must use +1 Force to move an Imperial starship away.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Filter twoSnubsFilter = Filters.and(Filters.system, Filters.wherePresent(self,
                Filters.and(Filters.your(self), Filters.piloted, Filters.snub_fighter, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.piloted, Filters.snub_fighter)))));

        Condition twoSnubsCondition = new DuringBattleWithParticipantCondition(2, Filters.and(Filters.your(self), Filters.piloted, Filters.snub_fighter));

        if (GameConditions.isAtLocation(game, self, Filters.war_room))
        {
            List<Modifier> modifiers = new LinkedList<Modifier>();
            modifiers.add(new MoveCostFromLocationModifier(self, Filters.and(Filters.your(opponent), Filters.Imperial_starship), 1, twoSnubsFilter));
            modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.system, opponent, twoSnubsCondition, playerId));
            modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, twoSnubsCondition, 1, opponent));
            return modifiers;
        }
        return null;
    }
}
