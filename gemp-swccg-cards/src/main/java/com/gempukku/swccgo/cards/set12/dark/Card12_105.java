package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Edcel Bar Gane
 */
public class Card12_105 extends AbstractRepublic {
    public Card12_105() {
        super(Side.DARK, 3, 3, 1, 2, 5, "Edcel Bar Gane", Uniqueness.UNIQUE);
        setPolitics(2);
        setLore("Senator from the planet Roona. One of the first to support Amidala's motion for a vote of no confidence in Supreme Chancellor Valorum. Intolerant of other species.");
        setGameText("Agenda: ambition. While in a senate majority, opponent may not draw more than one battle destiny in battles at battleground sites and you may not cancel opponent's battle destiny draws at sites.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition inSenateMajority = new InSenateMajorityCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.battleground_site, inSenateMajority, 1, opponent));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.site, opponent, inSenateMajority, playerId));
        return modifiers;
    }
}
