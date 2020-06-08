package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect records the attack being initiated at a location.
 */
class RecordAttackInitiatedEffect extends AbstractSuccessfulEffect {
    private String _performingPlayerId;
    private PhysicalCard _cardBeingAttacked;
    private PhysicalCard _creatureAttacking;

    /**
     * Creates an effect that records the attack being initiated at a location for the purposes of the game keeping
     * track of which cards were involved in the attack.
     * @param action the action performing this effect
     * @param cardBeingAttacked the card being attacked
     * @param creatureAttacking the creature attacking, otherwise null if non-creature is attacking a creature
     */
    public RecordAttackInitiatedEffect(Action action, PhysicalCard cardBeingAttacked, PhysicalCard creatureAttacking) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _cardBeingAttacked = cardBeingAttacked;
        _creatureAttacking = creatureAttacking;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _cardBeingAttacked);

        // Begin attack
        if (_creatureAttacking == null) {
            gameState.sendMessage(_performingPlayerId + " initiates attack on " + GameUtils.getCardLink(_cardBeingAttacked) + ", chosen randomly, at " + GameUtils.getCardLink(location));
        }
        else if (!Filters.creature.accepts(game, _cardBeingAttacked)) {
            gameState.sendMessage(GameUtils.getCardLink(_creatureAttacking) + " attacks " + GameUtils.getCardLink(_cardBeingAttacked) + ", chosen randomly");
            gameState.cardAffectsCard(_performingPlayerId, _creatureAttacking, _cardBeingAttacked);
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_creatureAttacking) + " and " + GameUtils.getCardLink(_cardBeingAttacked) + ", chosen randomly, attack each other");
        }
        gameState.beginAttack(_performingPlayerId, location, _cardBeingAttacked, _creatureAttacking);
    }
}
