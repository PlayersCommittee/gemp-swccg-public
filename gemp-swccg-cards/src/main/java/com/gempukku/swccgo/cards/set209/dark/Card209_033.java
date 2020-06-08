package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;


import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Bala-Tik
 */
public class Card209_033 extends AbstractAlien {
    public Card209_033() {
        super(Side.DARK, 2, 3, 4, 2, 5, "Bala-Tik", Uniqueness.UNIQUE);
        setLore("Gangster, information broker, and leader.");
        setGameText("[Pilot]2.  While with Han, he may not add battle destiny draws or play an Interrupt from Lost Pile.  While at opponent's battleground, opponent may not cancel or reduce your force drains here.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_9, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.INFORMATION_BROKER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String opponent = game.getOpponent(self.getOwner());

        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));

        // Creating a couple of conditions, to make the bit of code a more managable length.

        Condition atOpponentsBG = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground));
        Condition withHan = new WithCondition(self, Filters.Han);

        // Force Drains May Not Be Canceled:
        // - self: source of this text, this card.
        // - Filters.here(self): location where the force drain may not be canceled, where Bala-Tik is
        // - atOpponentsBG: self (Bala-Tik) is at opponent's battleground
        // - opponent: It is the opponent who may not cancel my force drains (still possible for me to cancel it)
        // - self.getOwner(): It is my force drains that can not be canceled by my opponent.

        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), atOpponentsBG, opponent, self.getOwner()));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), atOpponentsBG, opponent, self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Han, withHan, ModifyGameTextType.SOLO__MAY_NOT_PLAY_INTERRUPT_FROM_LOST_PILE));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.Han, withHan));

        return modifiers;
    }

}
