package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;


/**
 * Set: Premiere
 * Type: Effect
 * Title: A Disturbance In The Force
 */
public class Card1_208 extends AbstractNormalEffect {
    public Card1_208() {
        super(Side.DARK, 3, PlayCardZoneOption.OPPONENTS_RESERVE_DECK, "A Disturbance In The Force", Uniqueness.UNIQUE);
        setLore("The destruction of Alderaan caused a great disturbance in the Force '...as if millions of voices suddenly cried out in terror and were suddenly silenced.'");
        setGameText("Once per game, during your deploy phase, 'insert' (face down) into opponent's Reserve Deck; reshuffle. When effect reaches top it is immediately lost, but opponent may not activate any more Force that turn. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canInsertOncePerGame(game, self);
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Reveal 'insert' card");
        action.setActionMsg(null);
        // Perform result(s)
        action.appendEffect(
                new LoseInsertCardEffect(action, self));
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action,
                        new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_ACTIVATE_FORCE, opponent),
                        "Causes " + opponent + " to not activate any more Force"));
        return action;
    }
}
