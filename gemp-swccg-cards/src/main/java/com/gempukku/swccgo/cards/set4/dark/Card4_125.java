package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleModifier;


/**
 * Set: Dagobah
 * Type: Effect
 * Title: Knowledge And Defense
 */
public class Card4_125 extends AbstractNormalEffect {
    public Card4_125() {
        super(Side.DARK, 4, PlayCardZoneOption.OPPONENTS_RESERVE_DECK, "Knowledge And Defense", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'A Jedi uses the Force for knowledge and defense, never for attack.'");
        setGameText("'Insert' in opponent's Reserve Deck. When Effect reaches top it is lost, but opponent may not initiate any battles for remainder of turn. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Reveal 'insert' card");
        action.setActionMsg(null);
        // Perform result(s)
        action.appendEffect(
                new LoseInsertCardEffect(action, self));
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action,
                        new MayNotInitiateBattleModifier(self, opponent),
                        "Causes " + opponent + " to not initiate battles"));
        return action;
    }
}