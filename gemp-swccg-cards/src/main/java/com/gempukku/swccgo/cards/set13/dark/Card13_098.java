package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Wipe Them Out, All Of Them
 */
public class Card13_098 extends AbstractDefensiveShield {
    public Card13_098() {
        super(Side.DARK, "Wipe Them Out, All Of Them");
        setLore("Darth Sidious' command was merciless and direct. He left no room for misinterpretation.");
        setGameText("Plays on table. While opponent has a non-unique alien or non-unique starfighter in battle, opponent may not draw more than two battle destiny.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, new InBattleCondition(self, Filters.and(Filters.opponents(self),
                Filters.non_unique, Filters.or(Filters.alien, Filters.starfighter))), 2, opponent));
        return modifiers;
    }
}