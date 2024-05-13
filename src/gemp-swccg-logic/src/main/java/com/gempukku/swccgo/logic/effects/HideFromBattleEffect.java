package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToHideFromBattleResult;
import com.gempukku.swccgo.logic.timing.results.ExcludedFromBattleResult;

import java.util.Collections;

/**
 * An effect that causes a card to 'hide' from battle.
 */
public class HideFromBattleEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _cardToHide;
    private PhysicalCard _preventedCard;
    private HideFromBattleEffect _that;

    /**
     * Creates an effect that causes the specified card to 'hide' from battle.
     * @param action the action performing this effect
     * @param cardToHide the card to 'hide' from battle
     */
    public HideFromBattleEffect(Action action, PhysicalCard cardToHide) {
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

        // 1) Trigger "about to hide from battle" for card.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent the card from hiding from battle.
        subAction.appendEffect(new TriggeringResultEffect(subAction,
                new AboutToHideFromBattleResult(subAction, _action.getPerformingPlayer(), _cardToHide, _that)));

        // 2) Figure out if card will still 'hide' from battle
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_cardToHide.equals(_preventedCard))
                            return;

                        BattleState battleState = gameState.getBattleState();
                        if (battleState == null || !battleState.isCardParticipatingInBattle(_cardToHide))
                            return;

                        gameState.sendMessage(GameUtils.getCardLink(_cardToHide) + " 'hides' from battle");
                        game.getModifiersEnvironment().addUntilEndOfBattleModifier(new ExcludedFromBattleModifier(subAction.getActionSource(), _cardToHide));
                        battleState.updateParticipants(game);
                        game.getActionsEnvironment().emitEffectResult(new ExcludedFromBattleResult(subAction.getPerformingPlayer(), subAction.getActionSource(), Collections.singletonList(_cardToHide), null, null));
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
