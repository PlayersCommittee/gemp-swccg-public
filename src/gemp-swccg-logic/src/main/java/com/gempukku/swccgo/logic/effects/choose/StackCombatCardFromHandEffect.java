package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose and stack a combat card from hand on a specified card.
 */
public class StackCombatCardFromHandEffect extends StackCardFromHandEffect {

    /**
     * Creates an effect that causes the specified player to choose and stack a card from hand on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack a card on
     * @param cardToStack the card to stack
     */
    public StackCombatCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, PhysicalCard cardToStack) {
        super(action, playerId, stackOn, cardToStack, true, false, false, true);
    }

    public String getChoiceText() {
        return "Choose card to place as combat card";
    }
}