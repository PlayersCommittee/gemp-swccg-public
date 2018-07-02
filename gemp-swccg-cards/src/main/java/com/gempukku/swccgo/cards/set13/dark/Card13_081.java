package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Oppressive Enforcement
 */
public class Card13_081 extends AbstractDefensiveShield {
    public Card13_081() {
        super(Side.DARK, Title.Oppressive_Enforcement);
        setLore("The Imperial fleet keeps a tight grip on the systems under its control. Abuses and excesses by local citizens are not tolerated.");
        setGameText("Plays on table. Your Immediate Effects may play for free. Whenever opponent cancels your card with Sense or Alter, place that canceled card in Used Pile.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeModifier(self, Filters.and(Filters.your(self), Filters.Immediate_Effect)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.your(self), opponent, Filters.or(Filters.Sense, Filters.Alter)));
        return modifiers;
    }
}