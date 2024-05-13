package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Jloc'itaome'faottas
 */
public class Card304_058 extends AbstractAlien {
    public Card304_058() {
        super(Side.LIGHT, 2, 4, 2, 3, 5, "Jloc'itaome'faottas", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Locita, as he refers to himself in public, is an unknown gangster and information broker from the Unknown Regions. He's encouraged Claudius the Hutt to expand his business to Ulress.");
        setGameText("While with Thran, he may not add battle destiny draws or play an Interrupt from Lost Pile.  While at opponent's battleground, opponent may not cancel or reduce your force drains here.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.INFORMATION_BROKER);
		addPersona(Persona.LOCITA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String opponent = game.getOpponent(self.getOwner());

        // Creating a couple of conditions, to make the bit of code a more managable length.

        Condition atOpponentsBG = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground));
        Condition withThran = new WithCondition(self, Filters.Thran);

        // Force Drains May Not Be Canceled:
        // - self: source of this text, this card.
        // - Filters.here(self): location where the force drain may not be canceled, where Bala-Tik is
        // - atOpponentsBG: self (Bala-Tik) is at opponent's battleground
        // - opponent: It is the opponent who may not cancel my force drains (still possible for me to cancel it)
        // - self.getOwner(): It is my force drains that can not be canceled by my opponent.

        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), atOpponentsBG, opponent, self.getOwner()));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), atOpponentsBG, opponent, self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Thran, withThran, ModifyGameTextType.THRAN__MAY_NOT_PLAY_INTERRUPT_FROM_LOST_PILE));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.Thran, withThran));

        return modifiers;
    }

}
