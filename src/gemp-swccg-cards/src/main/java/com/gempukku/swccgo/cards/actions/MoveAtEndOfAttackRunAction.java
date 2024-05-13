package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.MoveAtEndOfAttackRunEffect;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * An action for a starfighter to move at end of an Attack Run.
 */
public class MoveAtEndOfAttackRunAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _locationToMoveTo;
    private Effect _moveCardEffect;
    private boolean _cardMoved;

    /**
     * Creates an action for a starfighter to move at end of an Attack Run.
     * @param playerId the player
     * @param card the card to move
     * @param deathStar the Death Star system
     */
    public MoveAtEndOfAttackRunAction(final String playerId, final PhysicalCard card, final PhysicalCard deathStar) {
        super(card, playerId);
        _locationToMoveTo = deathStar;
        _moveCardEffect = new MoveAtEndOfAttackRunEffect(this, card, deathStar);
    }

    @Override
    public String getText() {
        return "Move to " + GameUtils.getFullName(_locationToMoveTo);
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_cardMoved) {
                _cardMoved = true;
                return _moveCardEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardMoved && _moveCardEffect.wasCarriedOut();
    }
}
