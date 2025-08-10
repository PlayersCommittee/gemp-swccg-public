package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToUseCombatCardInsteadOfDestinyDrawResult;

/**
 * An effect that substitutes a combat card for the destiny draw.
 */
public class UseCombatCardForSubstituteDestinyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _card;
    private float _substituteValue;
    private PhysicalCard _preventedCard;
    private UseCombatCardForSubstituteDestinyEffect _that;

    /**
     * Creates an effect that substitutes the specified combat card for the destiny draw
     * @param action the action performing this effect.
     * @param card the card to use as the destiny draw
     * @param substituteValue the substitute value
     */
    public UseCombatCardForSubstituteDestinyEffect(Action action, PhysicalCard card, float substituteValue) {
        super(action);
        _card = card;
        _substituteValue = substituteValue;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final String playerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);

        // 1) Record combat card as being used
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        modifiersQuerying.combatCardUsed(playerId);
                        gameState.showCardOnScreen(_card);
                    }
                });

        // 2) Trigger "about to use combat card instead of destiny draw" for card.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent the card from being used instead of destiny draw.
        subAction.appendEffect(new TriggeringResultEffect(subAction,
                new AboutToUseCombatCardInsteadOfDestinyDrawResult(subAction, _action.getPerformingPlayer(), _card, _that)));

        // 3) Figure out if card will still be used instead of destiny draw
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_card.equals(_preventedCard))
                            return;

                        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
                        if (drawDestinyState != null) {
                            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
                            gameState.sendMessage(_action.getPerformingPlayer() + " substitutes combat card " + GameUtils.getCardLink(_card) + "'s destiny value of " + GuiUtils.formatAsString(_substituteValue)
                                    + " for " + drawDestinyEffect.getDestinyType().getHumanReadable());
                            drawDestinyEffect.setSubstituteDestiny(_substituteValue);
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
