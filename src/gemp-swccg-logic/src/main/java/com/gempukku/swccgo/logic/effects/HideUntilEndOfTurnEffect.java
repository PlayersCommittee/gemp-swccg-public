package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToHideFromBattleResult;
import com.gempukku.swccgo.logic.timing.results.ExcludedFromBattleResult;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.Collections;

/**
 * An effect that causes a card to 'hide' (may not participate in battle) until end of the turn.
 * Uses the same AboutToHideFromBattle path as {@link HideFromBattleEffect} so cards such as
 * Nice Of You Guys To Drop By can cancel the hide attempt.
 */
public class HideUntilEndOfTurnEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _cardToHide;
    private PhysicalCard _preventedCard;
    private HideUntilEndOfTurnEffect _that;

    /**
     * Creates an effect that causes the specified card to 'hide' until end of the turn.
     * @param action the action performing this effect
     * @param cardToHide the card to 'hide'
     */
    public HideUntilEndOfTurnEffect(Action action, PhysicalCard cardToHide) {
        super(action);
        _cardToHide = cardToHide;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger "about to hide from battle" for card (same family as HideFromBattleEffect).
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent the hide.
        subAction.appendEffect(new TriggeringResultEffect(subAction,
                new AboutToHideFromBattleResult(subAction, _action.getPerformingPlayer(), _cardToHide, _that)));

        // 2) Apply hide until end of turn if not prevented
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_cardToHide.equals(_preventedCard))
                            return;

                        PhysicalCard source = subAction.getActionSource();
                        gameState.sendMessage(GameUtils.getCardLink(_cardToHide) + " 'hides' (may not participate in battle) until end of the turn");
                        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, Collections.singleton(_cardToHide));

                        // If currently in a battle, exclude from that battle (same as HideFromBattleEffect)
                        BattleState battleState = gameState.getBattleState();
                        if (battleState != null && battleState.isCardParticipatingInBattle(_cardToHide)) {
                            game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                                    new ExcludedFromBattleModifier(source, _cardToHide));
                            battleState.updateParticipants(game);
                            game.getActionsEnvironment().emitEffectResult(
                                    new ExcludedFromBattleResult(subAction.getPerformingPlayer(), source,
                                            Collections.singletonList(_cardToHide), null, null));
                        }

                        // Remainder of turn: may not participate in battle
                        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToHide), Filters.in_play);
                        game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                                new MayNotParticipateInBattleModifier(source, cardFilter));

                        game.getActionsEnvironment().emitEffectResult(
                                new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToHide));
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
