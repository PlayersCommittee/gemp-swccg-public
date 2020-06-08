package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An effect to 'hit' a card.
 */
public class HitCardEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _cardHit;
    private PhysicalCard _hitByCard;
    private SwccgBuiltInCardBlueprint _hitByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private HitCardEffect _that;

    /**
     * Creates an effect 'hit' a card.
     * @param action the action performing this effect
     * @param cardHit the card that is hit
     * @param hitByCard the card the card was hit by
     */
    public HitCardEffect(Action action, PhysicalCard cardHit, PhysicalCard hitByCard) {
        this(action, cardHit, hitByCard, null, null);
    }

    /**
     * Creates an effect 'hit' a card.
     * @param action the action performing this effect
     * @param cardHit the card that is hit
     * @param hitByCard the card the card was hit by
     * @param hitByPermanentWeapon the permanent weapon that hit the card
     * @param cardFiringWeapon the card that fired the weapon
     */
    public HitCardEffect(Action action, PhysicalCard cardHit, PhysicalCard hitByCard, SwccgBuiltInCardBlueprint hitByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(action);
        _cardHit = cardHit;
        _hitByCard = hitByCard;
        _hitByPermanentWeapon = hitByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _that = this;
    }

    @Override
    public String getText(SwccgGame game) {
        return "'Hit' " + GameUtils.getCardLink(_cardHit);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

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
                                            gameState.sendMessage(GameUtils.getCardLink(_cardHit) + " is 'hit' by " + GameUtils.getCardLink(_hitByCard));
                                            game.getGameState().cardAffectsCard(_hitByCard.getOwner(), _hitByCard, _cardHit);

                                            _cardHit.setHit(true);
                                            if (!_cardHit.isSideways()) {
                                                gameState.turnCardSideways(game, _cardHit, false);
                                            }

                                            game.getActionsEnvironment().emitEffectResult(new HitResult(_cardHit, _hitByCard, _hitByPermanentWeapon, _cardFiringWeapon));
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
