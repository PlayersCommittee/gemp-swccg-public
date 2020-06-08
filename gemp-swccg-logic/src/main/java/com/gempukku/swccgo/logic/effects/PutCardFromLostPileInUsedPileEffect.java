package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

/*
 This action is for putting a card from Lost Pile in Used Pile
 when it is not a "retrieve Force" action.
 Example: Myo "regenerates" to Used Pile, or Evader relocates just lost card to Used Pile.
 */
public class PutCardFromLostPileInUsedPileEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _card;
    private PhysicalCard _preventedCard;
    private boolean _justLost;
    private boolean _asCaptureEscape;
    private PutCardFromLostPileInUsedPileEffect _that;

    // TODO: Update these effects to be as generic as needed

    public PutCardFromLostPileInUsedPileEffect(Action action, String playerId, PhysicalCard card, boolean justLost) {
        this(action, playerId, card, justLost, false);
    }

    public PutCardFromLostPileInUsedPileEffect(Action action, String playerId, PhysicalCard card, boolean justLost, boolean asCaptureEscape) {
        super(action);
        _playerId = playerId;
        _card = card;
        _justLost = justLost;
        _asCaptureEscape = asCaptureEscape;
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

                            if (!_asCaptureEscape) {
                                gameState.sendMessage(_playerId + " places " + GameUtils.getCardLink(_card) + " in Used Pile");
                            }
                            gameState.removeCardsFromZone(Collections.singleton(_card));
                            gameState.addCardToTopOfZone(_card, Zone.USED_PILE, _card.getOwner());

                            if (_asCaptureEscape) {
                                actionsEnvironment.emitEffectResult(
                                        new CaptureCharacterResult(_playerId, _action.getActionSource(), null, _card, false, false, CaptureOption.ESCAPE));
                            }
                            else {
                                actionsEnvironment.emitEffectResult(
                                        new PutCardInCardPileFromOffTableResult(_action, _card, _card.getZoneOwner(), Zone.USED_PILE, false));
                            }
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
