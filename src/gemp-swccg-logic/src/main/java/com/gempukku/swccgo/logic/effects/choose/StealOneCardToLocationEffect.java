package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultsEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;

import java.util.*;

/**
 * An effect that carries out the stealing of a single card at a location to the new owner's side of the location.
 */
class StealOneCardToLocationEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _cardToBeStolen;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private StealOneCardToLocationEffect _that;

    /**
     * Creates an effect that carries out the stealing of a single card at a location to the new owner's side of the location.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealOneCardToLocationEffect(Action action, PhysicalCard cardToSteal) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardToBeStolen = cardToSteal;
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

        // 1) Trigger is "about to be stolen" for cards specified cards to be stolen.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being stolen.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        effectResults.add(new AboutToBeStolenResult(subAction, _cardToBeStolen, _that));
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Make any characters aboard (attached to) lost
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!isEffectOnCardPrevented(_cardToBeStolen)) {

                            Collection<PhysicalCard> allCharactersAboardStolenCards = Filters.filter(gameState.getAllAttachedRecursively(_cardToBeStolen), game, Filters.character);
                            if (!allCharactersAboardStolenCards.isEmpty()) {

                                SubAction makeCharactersAboardLostSubAction = new SubAction(subAction);
                                makeCharactersAboardLostSubAction.appendEffect(
                                        new LoseCardsFromTableSimultaneouslyEffect(subAction, allCharactersAboardStolenCards, true, true));
                                game.getActionsEnvironment().addActionToStack(makeCharactersAboardLostSubAction);
                            }
                        }
                    }
                }
        );

        // 3) Steal the card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!isEffectOnCardPrevented(_cardToBeStolen)) {

                            List<PhysicalCard> allCardsStolen = new LinkedList<PhysicalCard>();
                            allCardsStolen.add(_cardToBeStolen);
                            allCardsStolen.addAll(gameState.getAllAttachedRecursively(_cardToBeStolen));

                            gameState.sendMessage(_playerId + " steals " + GameUtils.getCardLink(_cardToBeStolen) + " using " + GameUtils.getCardLink(_action.getActionSource()));
                            PhysicalCard stolenFromLocation = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _cardToBeStolen);

                            // Update owner and zone owner of each card, then attach card
                            for (PhysicalCard card : allCardsStolen) {
                                card.setOwner(_playerId);
                                card.setZoneOwner(_playerId);
                            }
                            game.getGameState().moveCardToLocation(_cardToBeStolen, modifiersQuerying.getLocationThatCardIsAt(gameState, _cardToBeStolen));

                            for (PhysicalCard card : allCardsStolen) {
                                gameState.reapplyAffectingForCard(game, card);
                            }
                            // Emit effect result for each stolen card
                            game.getActionsEnvironment().emitEffectResult(new StolenResult(_playerId, _cardToBeStolen, stolenFromLocation));
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
