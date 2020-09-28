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
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An effect to 'hit' a card and causes it to not be able to be used to satisfy attrition and causes opponent to lose force
 */
public class HitCardAndMayNotBeUsedToSatisfyAttritionAndOpponentLosesForceEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _cardHit;
    private PhysicalCard _hitByCard;
    private SwccgBuiltInCardBlueprint _hitByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private HitCardAndMayNotBeUsedToSatisfyAttritionAndOpponentLosesForceEffect _that;
    private Filter _forceLossFilter;
    private float _forceLossAmount;

    /**
     * Creates an to 'hit' a card and causes it to not be able to be used to satisfy attrition.
     * @param action the action performing this effect
     * @param cardHit the card that is hit and not able to satisfy attrition
     * @param hitByCard the card the card was hit by
     * @param hitByPermanentWeapon the permanent weapon that hit the card
     * @param cardFiringWeapon the card that fired the weapon
     * @param forceLossFilter opponent loses force if weapon firing successful against card accepted by the filter
     * @param forceLossAmount the amount of force to lose
     */
    public HitCardAndMayNotBeUsedToSatisfyAttritionAndOpponentLosesForceEffect(Action action, PhysicalCard cardHit, PhysicalCard hitByCard, SwccgBuiltInCardBlueprint hitByPermanentWeapon, PhysicalCard cardFiringWeapon, Filter forceLossFilter, float forceLossAmount) {
        super(action);
        _cardHit = cardHit;
        _hitByCard = hitByCard;
        _hitByPermanentWeapon = hitByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _that = this;
        _forceLossFilter = forceLossFilter;
        _forceLossAmount = forceLossAmount;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent).accepts(game, _cardHit)) {
                            return;
                        }

                        // 1) Trigger is "about to be 'hit'" for cards specified cards to be 'hit'.
                        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being 'hit'.
                        List<EffectResult> effectResults = new ArrayList<EffectResult>();
                        effectResults.add(new AboutToBeHitResult(subAction, _cardHit, _hitByCard, _hitByPermanentWeapon, _cardFiringWeapon, _that));
                        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

                        // 2) Check to continue if card should be 'hit'
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (!isEffectOnCardPrevented(_cardHit) && Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent).accepts(game, _cardHit)) {
                                            ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
                                            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
                                            PhysicalCard source = _action.getActionSource();

                                            gameState.sendMessage(GameUtils.getCardLink(_cardHit) + " is 'hit' by " + GameUtils.getCardLink(_hitByCard) + " and may not be used to satisfy attrition");
                                            gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _cardHit);

                                            _cardHit.setHit(true);
                                            if (!_cardHit.isSideways()) {
                                                gameState.turnCardSideways(game, _cardHit, false);
                                            }

                                            actionsEnvironment.emitEffectResult(new HitResult(_cardHit, _hitByCard, _hitByPermanentWeapon, _cardFiringWeapon));

                                            // Filter for same card while it is in play
                                            Filter cardFilter = Filters.and(Filters.sameCardId(_cardHit), Filters.or(Filters.onTable, Filters.canBeTargetedByWeaponAsIfPresent));

                                            Modifier modifier = new MayNotBeUsedToSatisfyAttritionModifier(source, cardFilter);
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
                                            actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardHit));


                                            String opponent = game.getOpponent(_action.getPerformingPlayer());
                                            if(_forceLossFilter!=null&&_forceLossFilter.accepts(game,_cardHit)&&_forceLossAmount>0) {
                                                subAction.appendEffect(
                                                        new LoseForceEffect(subAction, opponent, _forceLossAmount));
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
