package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An effect to 'hit' a card and resets its forfeit value, and causes both players to lose Force.
 */
public class HitCardResetForfeitAndBothPlayersLoseForceEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _cardHitAndReset;
    private float _resetValue;
    private float _forceLoss;
    private PhysicalCard _hitByCard;
    private SwccgBuiltInCardBlueprint _hitByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private HitCardResetForfeitAndBothPlayersLoseForceEffect _that;

    /**
     * Creates an effect to 'hit' a card, resets its forfeit value, and causes both players to lose Force.
     * @param action the action performing this effect
     * @param cardHitAndReset the card that is hit and whose forfeit value is reset
     * @param resetValue the reset value
     * @param forceLoss the amount of Force opponent loses
     * @param hitByCard the card the card was hit by
     * @param hitByPermanentWeapon the permanent weapon that hit the card
     * @param cardFiringWeapon the card that fired the weapon
     */
    public HitCardResetForfeitAndBothPlayersLoseForceEffect(Action action, PhysicalCard cardHitAndReset, float resetValue, float forceLoss, PhysicalCard hitByCard, SwccgBuiltInCardBlueprint hitByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(action);
        _cardHitAndReset = cardHitAndReset;
        _resetValue = resetValue;
        _forceLoss = forceLoss;
        _hitByCard = hitByCard;
        _hitByPermanentWeapon = hitByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final String playerId = _action.getPerformingPlayer();
        final String opponent = game.getOpponent(playerId);

        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent).accepts(game, _cardHitAndReset)) {
                            return;
                        }

                        // 1) Trigger is "about to be 'hit'" for cards specified cards to be 'hit'.
                        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being 'hit'.
                        List<EffectResult> effectResults = new ArrayList<EffectResult>();
                        effectResults.add(new AboutToBeHitResult(subAction, _cardHitAndReset, _hitByCard, _hitByPermanentWeapon, _cardFiringWeapon, _that));
                        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

                        // 2) Check to continue if card should be 'hit'
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (!isEffectOnCardPrevented(_cardHitAndReset) && Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent).accepts(game, _cardHitAndReset)) {
                                            // Check if card's forfeit may not be reduced
                                            boolean resetForfeit = true;
                                            float currentForfeit = modifiersQuerying.getForfeit(gameState, _cardHitAndReset);
                                            if (_resetValue < currentForfeit && modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, _cardHitAndReset)) {
                                                gameState.sendMessage(GameUtils.getCardLink(_cardHitAndReset) + " is 'hit' by " + GameUtils.getCardLink(_hitByCard) + " but its forfeit is prevented from being reduced to " + GuiUtils.formatAsString(_resetValue));
                                                resetForfeit = false;
                                            } else {
                                                gameState.sendMessage(GameUtils.getCardLink(_cardHitAndReset) + " is 'hit' by " + GameUtils.getCardLink(_hitByCard) + " and its forfeit is reset to " + GuiUtils.formatAsString(_resetValue));
                                            }

                                            ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
                                            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
                                            PhysicalCard source = _action.getActionSource();

                                            gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _cardHitAndReset);
                                            game.getModifiersQuerying().hitOrMadeLostByWeapon(_cardHitAndReset, _hitByCard);

                                            _cardHitAndReset.setHit(true);
                                            if (!_cardHitAndReset.isSideways()) {
                                                gameState.turnCardSideways(game, _cardHitAndReset, false);
                                            }

                                            actionsEnvironment.emitEffectResult(new HitResult(_cardHitAndReset, _hitByCard, _hitByPermanentWeapon, _cardFiringWeapon));

                                            if (resetForfeit) {
                                                // Filter for same card while it is in play
                                                Filter cardFilter = Filters.and(Filters.sameCardId(_cardHitAndReset), Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent));

                                                Modifier modifier = new ResetForfeitModifier(source, cardFilter, _resetValue);
                                                modifier.skipSettingNotRemovedOnRestoreToNormal();

                                                // If during battle and the source if the action is not a weapon, then reset until end of the battle, otherwise
                                                // lasts for remainder of game (until card leaves play).
                                                if (gameState.isDuringBattle()
                                                        && _hitByPermanentWeapon == null &&
                                                        !Filters.weapon.accepts(gameState, modifiersQuerying, _hitByCard)) {
                                                    modifiersEnvironment.addUntilEndOfBattleModifier(modifier);
                                                } else {
                                                    modifiersEnvironment.addUntilEndOfGameModifier(modifier);
                                                }
                                                actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardHitAndReset));
                                            }

                                            if (_forceLoss > 0) {
                                                if (gameState.getCurrentPlayerId().equals(playerId)) {
                                                    subAction.appendEffect(
                                                            new LoseForceEffect(subAction, opponent, _forceLoss));
                                                    subAction.appendEffect(
                                                            new LoseForceEffect(subAction, playerId, _forceLoss));
                                                } else {
                                                    subAction.appendEffect(
                                                            new LoseForceEffect(subAction, playerId, _forceLoss));
                                                    subAction.appendEffect(
                                                            new LoseForceEffect(subAction, opponent, _forceLoss));
                                                }
                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }
}
