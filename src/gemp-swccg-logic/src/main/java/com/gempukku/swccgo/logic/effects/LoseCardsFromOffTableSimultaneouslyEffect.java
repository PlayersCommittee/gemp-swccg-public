package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostCardFromOffTableResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be lost simultaneously.
 * This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
public class LoseCardsFromOffTableSimultaneouslyEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _originalCardsToLose;
    private String _playerToChooseOrder;
    private boolean _toBottomOfPile;

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be lost
     * simultaneously.
     * @param action the action performing this effect
     * @param cardsToLose the cards to lose
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public LoseCardsFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToLose, boolean toBottomOfPile) {
        this(action, cardsToLose, null, toBottomOfPile);
    }

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be lost
     * simultaneously.
     * @param action the action performing this effect
     * @param cardsToLose the cards to lose
     * @param playerToChooseOrder the player to choose order for all cards placed in Lost Pile, null if owner's choose order
     */
    public LoseCardsFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToLose, String playerToChooseOrder, boolean toBottomOfPile) {
        super(action);
        _originalCardsToLose = Collections.unmodifiableCollection(cardsToLose);
        _playerToChooseOrder = playerToChooseOrder;
        _toBottomOfPile = toBottomOfPile;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);

        // 1) Remove the cards from the existing zone.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _originalCardsToLose = Filters.filter(_originalCardsToLose, game, Filters.not(Filters.or(Filters.onTable, Filters.inLostPile)));
                        if (!_originalCardsToLose.isEmpty()) {

                            // Remove cards from existing zone
                            gameState.removeCardsFromZone(_originalCardsToLose);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _originalCardsToLose) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                        }
                    }
                }
        );

        // 2) Choose order that cards are placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToLose.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _originalCardsToLose, Zone.LOST_PILE, _playerToChooseOrder, _toBottomOfPile));
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 6) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToLose.isEmpty()) {

                            for (PhysicalCard lostCard : _originalCardsToLose) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new LostCardFromOffTableResult(subAction, lostCard));
                            }
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
