package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultsEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;

import java.util.*;

/**
 * An effect that carries out the stealing of a single card (and place it in the player's hand).
 */
public class StealOneCardIntoHandEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _cardToBeStolen;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private StealOneCardIntoHandEffect _that;

    /**
     * Creates an effect that carries out the stealing of a single card (and place it in the player's hand).
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealOneCardIntoHandEffect(Action action, PhysicalCard cardToSteal) {
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

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be stolen" for cards specified cards to be stolen.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being stolen.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        effectResults.add(new AboutToBeStolenResult(subAction, _cardToBeStolen, _that));
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Make any cards attached lost
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!isEffectOnCardPrevented(_cardToBeStolen)) {

                            Collection<PhysicalCard> allAttachedToStolenCards = gameState.getAllAttachedRecursively(_cardToBeStolen);
                            if (!allAttachedToStolenCards.isEmpty()) {

                                SubAction makeCharactersAboardLostSubAction = new SubAction(subAction);
                                makeCharactersAboardLostSubAction.appendEffect(
                                        new LoseCardsFromTableSimultaneouslyEffect(subAction, allAttachedToStolenCards, true, true));
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

                            String fromZoneText = Filters.onTable.accepts(game, _cardToBeStolen) ? "" : (" from opponent's " + _cardToBeStolen.getZone().getHumanReadable());
                            gameState.sendMessage(_playerId + " steals " + GameUtils.getCardLink(_cardToBeStolen) + fromZoneText + " into hand using " + GameUtils.getCardLink(_action.getActionSource()));
                            PhysicalCard stolenFromLocation = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _cardToBeStolen);

                            gameState.removeCardsFromZone(Collections.singleton(_cardToBeStolen));
                            _cardToBeStolen.setOwner(_playerId);
                            gameState.addCardToZone(_cardToBeStolen, Zone.HAND, _playerId);
                            gameState.reapplyAffectingForCard(game, _cardToBeStolen);

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
