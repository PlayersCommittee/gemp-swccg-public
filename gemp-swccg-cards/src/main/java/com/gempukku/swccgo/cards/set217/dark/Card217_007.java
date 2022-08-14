package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Republic
 * Title: Coruscant Guard (V)
 */
public class Card217_007 extends AbstractRepublic {
    public Card217_007() {
        super(Side.DARK, 3, 2, 2, 1, 4, "Coruscant Guard");
        setVirtualSuffix(true);
        setLore("Coruscant Guards are an elite force whose assignments include the protection of important political figures, as well as the policing of Coruscant's higher profile city districts.");
        setGameText("While present at Galactic Senate, all characters without politics here are forfeit = 0 (except Coruscant Guards) and neither player may draw more than one battle destiny here.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.CORUSCANT_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetForfeitModifier(self, Filters.and(Filters.character_without_politics, Filters.here(self),
                Filters.except(Filters.Coruscant_Guard)), new PresentAtCondition(self, Filters.Galactic_Senate), 0));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new PresentAtCondition(self, Filters.Galactic_Senate), 1, self.getOwner()));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new PresentAtCondition(self, Filters.Galactic_Senate), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
