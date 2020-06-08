package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayMoveUsingLandspeedCostEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DisembarkEffect;
import com.gempukku.swccgo.logic.effects.MoveUsingLandspeedEffect;
import com.gempukku.swccgo.logic.effects.MovingAsReactEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An action to move a card using landspeed.
 */
public class MoveUsingLandspeedAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private boolean _forFree;
    private float _changeInCost;
    private boolean _asReact;
    private boolean _asMoveAway;
    private boolean _disembarkedIfNeeded;
    private StandardEffect _disembarkEffect;
    private PhysicalCard _siteToMoveTo;
    private boolean _useForceCostApplied;
    private MovingAsReactEffect _moveCardEffect;
    private boolean _cardMoved;
    private Action _that;

    /**
     * Creates an action to move a card using landspeed.
     * @param playerId the player
     * @param game the game
     * @param card the card to move
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param moveTargetFilter the filter for where the card can be move
     */
    public MoveUsingLandspeedAction(final String playerId, final SwccgGame game, final PhysicalCard card, boolean forFree, float changeInCost, boolean asReact, boolean asMoveAway, final Filter moveTargetFilter) {
        super(card, playerId);
        _cardToMove = card;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _asReact = asReact;
        _asMoveAway = asMoveAway;
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to move " + GameUtils.getCardLink(card) + " using landspeed", moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _siteToMoveTo = selectedCard;
                        PhysicalCard siteToMoveFrom = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), _cardToMove);

                        if (_asMoveAway && _cardToMove.getAttachedTo() != null) {
                            _disembarkEffect = new DisembarkEffect(_that, card, siteToMoveFrom, false, false);
                        }
                        _moveCardEffect = new MoveUsingLandspeedEffect(_that, card, siteToMoveFrom, _siteToMoveTo, _asReact, _asMoveAway);
                    }
                }
        );
    }

    @Override
    public String getText() {
        return "Move using landspeed";
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
                if (!_forFree && !_asReact) {
                    appendCost(new PayMoveUsingLandspeedCostEffect(_that, getPerformingPlayer(), _cardToMove, _siteToMoveTo, false, _changeInCost));
                    return getNextCost();
                }
            }

            if (!_disembarkedIfNeeded) {
                _disembarkedIfNeeded = true;
                if (_disembarkEffect != null) {
                    appendCost(_disembarkEffect);
                    return getNextCost();
                }
            }

            if (!_cardMoved) {
                _cardMoved = true;

                // Set the move as 'react' effect
                if (_asReact) {
                    game.getGameState().getMoveAsReactState().setMovingAsReactEffect(_moveCardEffect);
                }

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
