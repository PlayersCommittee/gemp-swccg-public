package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationModifier;

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
        super(Side.LIGHT, 3, 2, 1, 2, 3, "General Walex Blissex", Uniqueness.UNIQUE);
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

        Filter yourPilotedSnubFighter = Filters.and(Filters.your(self), Filters.piloted, Filters.snub_fighter);
        Filter systemWithTwoPilotedSnubFighters = Filters.and(Filters.system, Filters.wherePresent(self,
                Filters.and(yourPilotedSnubFighter, Filters.presentWith(self, yourPilotedSnubFighter))));
        Condition atWarRoomCondition = new AtCondition(self, Filters.war_room);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.and(Filters.your(opponent), Filters.Imperial_starship), atWarRoomCondition, 1, systemWithTwoPilotedSnubFighters));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, systemWithTwoPilotedSnubFighters, opponent, atWarRoomCondition, playerId));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, systemWithTwoPilotedSnubFighters, atWarRoomCondition, 1, opponent));
        return modifiers;
    }
}
