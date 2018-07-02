package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.CardState;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromCardPileResult;

import java.util.Collections;

public class StackOneCardFromLostPileEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _card;
    private PhysicalCard _stackOn;
    private PhysicalCard _preventedCard;
    private boolean _faceDown;
    private boolean _asInactive;
    private boolean _justLost;
    private StackOneCardFromLostPileEffect _that;

    public StackOneCardFromLostPileEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown, boolean asInactive, boolean justLost) {
        super(action);
        _card = card;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _asInactive = asInactive;
        _justLost = justLost;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to remove just-lost card from Lost Pile" if for just-lost card.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified card from being removed from Lost Pile.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_justLost && GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.LOST_PILE) {
                            // Emit effect result that attempting to remove a just lost card from Lost Pile
                            actionsEnvironment.emitEffectResult(
                                    new AboutToRemoveJustLostCardFromLostPileResult(subAction, _action.getPerformingPlayer(), _card, _that));
                        }
                    }
                }
        );

        // 2) If not prevented, continue stacking card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if the card was prevented from being removed from Lost Pile
                        if (!isEffectOnCardPrevented(_card) && GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.LOST_PILE) {

                            String cardInfo = (!_faceDown || !_justLost || (_card.getZone() == Zone.TOP_OF_LOST_PILE && !gameState.isLostPileTurnedOver(_card.getZoneOwner()))) ? GameUtils.getCardLink(_card) : "a card";
                            gameState.sendMessage(_stackOn.getOwner() + " stacks " + cardInfo + " from Lost Pile " + (_faceDown ? "face down " : "") + "on " + GameUtils.getCardLink(_stackOn));
                            gameState.removeCardsFromZone(Collections.singleton(_card));
                            gameState.stackCard(_card, _stackOn, _faceDown, !_faceDown && _asInactive && (_card.getPreviousCardState() == CardState.ACTIVE || _card.getPreviousCardState() == CardState.INACTIVE), false);
                            actionsEnvironment.emitEffectResult(
                                    new StackedFromCardPileResult(_action, _card, _stackOn));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCard == null;
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCard = card;
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return card.equals(_preventedCard);
    }
}
