package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayMoveToStartBombingRunEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.MoveToStartBombingRunEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * An action for a bomber to move when starting a Bombing Run.
 */
public class MoveToStartBombingRunAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private boolean _forFree;
    private PhysicalCard _locationToMoveFrom;
    private PhysicalCard _locationToMoveTo;
    private boolean _useForceCostApplied;
    private Effect _moveCardEffect;
    private boolean _cardMoved;
    private Action _that;

    /**
     * Creates an action for a bomber to move when starting a Bombing Run.
     * @param playerId the player
     * @param card the card to move
     * @param moveTargetFilter the filter for where the card can be move
     */
    public MoveToStartBombingRunAction(final String playerId, final PhysicalCard card, final Filter moveTargetFilter) {
        super(card, playerId);
        _cardToMove = card;
        _locationToMoveFrom = card.getAtLocation();
        _forFree = false;
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to move " + GameUtils.getCardLink(card) + " to start Bombing Run", moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _locationToMoveTo = selectedCard;

                        _moveCardEffect = new MoveToStartBombingRunEffect(_that, card, _locationToMoveTo);
                    }
                }
        );
    }

    @Override
    public String getText() {
        return "Start Bombing Run";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayMoveToStartBombingRunEffect(_that, getPerformingPlayer(), _cardToMove, _locationToMoveFrom, _locationToMoveTo, 0));
                    return getNextCost();
                }
            }

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
