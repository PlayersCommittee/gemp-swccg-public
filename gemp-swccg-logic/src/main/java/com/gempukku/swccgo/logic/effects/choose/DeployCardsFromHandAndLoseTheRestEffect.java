package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the player performing the action to deploy any of the specified cards in hand that can be deployed,
 * and lose the rest of the specified cards.
 */
public class DeployCardsFromHandAndLoseTheRestEffect extends AbstractSubActionEffect {
    private String _playerId;
    private List<PhysicalCard> _remainingCards;
    private Filterable _deployableFilter;
    private boolean _forFree;

    /**
     * Creates an effect that causes the player performing the action to deploy any of the specified cards in hand that
     * can be deployed, and lose the rest of the specified cards.
     * @param action the action performing this effect
     * @param cards the specified cards in hand
     * @param deployableFilter the filter for cards that can be deployed
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardsFromHandAndLoseTheRestEffect(Action action, Collection<PhysicalCard> cards, Filterable deployableFilter, boolean forFree) {
        this(action, cards, deployableFilter, null, forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy any of the specified cards in hand that
     * can be deployed, and lose the rest of the specified cards.
     * @param action the action performing this effect
     * @param cards the specified cards in hand
     * @param deployableFilter the filter for cards that can be deployed
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardsFromHandAndLoseTheRestEffect(Action action, Collection<PhysicalCard> cards, Filterable deployableFilter, Filter specialLocationConditions, boolean forFree) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _remainingCards = new LinkedList<PhysicalCard>(cards);
        _deployableFilter = Filters.and(deployableFilter, Filters.inHand(_playerId), Filters.deployable(_action.getActionSource(), specialLocationConditions, forFree, 0));
        _forFree = forFree;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> deployableCards = Filters.filter(_remainingCards, game, _deployableFilter);
                        if (!deployableCards.isEmpty()) {
                            subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, deployableCards));
                        }
                    }
                }
        );
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardsToLose = Filters.filter(_remainingCards, game, Filters.inHand(_playerId));
                        if (!cardsToLose.isEmpty()) {
                            subAction.appendEffect(
                                    new LoseCardsFromHandEffect(subAction, _playerId, cardsToLose));
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction, Collection<PhysicalCard> deployableCards) {
        return new ChooseCardFromHandEffect(subAction, _playerId, Filters.in(deployableCards), true) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to deploy";
            }
            @Override
            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                _remainingCards.remove(selectedCard);
                subAction.insertEffect(
                        new DeployCardFromHandEffect(subAction, selectedCard, _forFree),
                        new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Collection<PhysicalCard> deployableCards = Filters.filter(_remainingCards, game, Filters.and(_deployableFilter));
                                if (!deployableCards.isEmpty()) {
                                    subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, deployableCards));
                                }
                            }
                        }
                );
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}



