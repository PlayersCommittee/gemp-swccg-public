package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromOffTableResult;

import java.util.*;

/**
 * An effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed out of play simultaneously.
 * This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class PlaceCardsOutOfPlayFromOffTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToPlaceOutOfPlay;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private PlaceCardsOutOfPlayFromOffTableSimultaneouslyEffect _that;
    private Collection<PhysicalCard> _placedOutOfPlay = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed out of
     * play simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceOutOfPlay the cards to place out of play
     */
    public PlaceCardsOutOfPlayFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceOutOfPlay) {
        super(action);
        _originalCardsToPlaceOutOfPlay = Collections.unmodifiableCollection(cardsToPlaceOutOfPlay);
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

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be placed out of play" for cards specified cards to be placed out of play.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being placed out of play.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToPlaceOutOfPlay : _originalCardsToPlaceOutOfPlay) {
            effectResults.add(new AboutToPlaceCardOutOfPlayFromOffTableResult(subAction, cardToPlaceOutOfPlay, _that));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Remove the cards from the existing zone.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _placedOutOfPlay.addAll(Filters.filter(_originalCardsToPlaceOutOfPlay, game, Filters.not(Filters.or(Filters.onTable, Filters.outOfPlay, Filters.stackedOn(null, Filters.grabber), Filters.in(_preventedCards), Zone.OUT_OF_PLAY))));
                        if (!_placedOutOfPlay.isEmpty()) {

                            // Remove cards from existing zone
                            gameState.removeCardsFromZone(_placedOutOfPlay);

                            if (!_placedOutOfPlay.isEmpty()) {
                                if (_action.getPerformingPlayer() != null)
                                    game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_placedOutOfPlay) + " to be placed out of play using " + GameUtils.getCardLink(_action.getActionSource()));
                                else
                                    game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_placedOutOfPlay) + " to be placed out of play");
                            }

                            // Place cards out of play.
                            for (PhysicalCard card : _placedOutOfPlay) {
                                gameState.addCardToZone(card, Zone.OUT_OF_PLAY, card.getOwner());
                            }
                        }
                    }
                }
        );

        // 3) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_placedOutOfPlay.isEmpty()) {

                            for (PhysicalCard card : _placedOutOfPlay) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new PlacedCardOutOfPlayFromOffTableResult(subAction, card));
                            }
                        }
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
