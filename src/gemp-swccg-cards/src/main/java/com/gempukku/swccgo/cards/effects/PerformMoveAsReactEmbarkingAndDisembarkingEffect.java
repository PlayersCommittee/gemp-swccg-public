package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.cards.actions.MoveAsReactAction;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.MoveAsReactState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect for performing the embarking and disembarking actions that may occurring during a move as a 'react'.
 */
public class PerformMoveAsReactEmbarkingAndDisembarkingEffect extends AbstractSubActionEffect {
    private MoveAsReactAction _moveAsReactAction;

    /**
     * Creates an effect for performing the embarking and disembarking actions that may occurring during a move as a 'react'.
     * @param moveAsReactAction the action performing this effect
     */
    public PerformMoveAsReactEmbarkingAndDisembarkingEffect(MoveAsReactAction moveAsReactAction) {
        super(moveAsReactAction);
        _moveAsReactAction = moveAsReactAction;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getNextSubActionEffect(subAction));
        return subAction;
    }

    /**
     * Gets the next effect to perform.
     * @param subAction the sub-action performing the effect
     * @return the effect to perform
     */
    private StandardEffect getNextSubActionEffect(final SubAction subAction) {
        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();
                if (moveAsReactState == null || !moveAsReactState.canContinue()) {
                    return;
                }

                List<Action> actions = new LinkedList<Action>();
                String movementText = null;

                // Figure out each action that can be done based on whether this is before or after the "regular move" of the reacting card.
                //
                // The following types of movements are allowed before regular move of the 'react'
                // 1) For the reacting card itself, it may disembark.
                // 2) For other cards with the reacting card, they may embark on the reacting card.
                //
                // The following types of movements are allowed after regular move of the 'react'
                // 1) For the reacting card itself, it may embark.
                // 2) For other cards aboard the reacting card, they may disembark from the reacting card.
                PhysicalCard reactingCard = moveAsReactState.getReactingCard();
                boolean isPassNotAllowed = false;

                // Before the regular move of the 'react'
                if (moveAsReactState.isDuringPreMovements()) {

                    // Determine if the reacting card is able to performing its regular move
                    isPassNotAllowed = (_moveAsReactAction.getMoveAsReactRegularMoveAction(game) == null);
                    movementText = "Perform a movement before regular move of the 'react'" + (!isPassNotAllowed ? " or Pass" : "");

                    // Get disembark action for the reacting card
                    Action disembarkAction = reactingCard.getBlueprint().getDisembarkAction(reactingCard.getOwner(), game, reactingCard, true, false, Filters.any);
                    if (disembarkAction != null) {
                        actions.add(disembarkAction);
                    }

                    // Get any embark actions for other cards with the reacting card
                    Collection<PhysicalCard> cardsWithReactingCard = Filters.filterActive(game, null,
                            Filters.and(Filters.your(reactingCard), Filters.atSameLocation(reactingCard), Filters.notPreventedFromParticipatingInReact));
                    for (PhysicalCard otherCard : cardsWithReactingCard) {
                        Action embarkAction = otherCard.getBlueprint().getEmbarkAction(otherCard.getOwner(), game, otherCard, true, Filters.sameCardId(reactingCard));
                        if (embarkAction != null) {
                            actions.add(embarkAction);
                        }
                    }
                }
                // After the regular move of the 'react'
                else if (moveAsReactState.isDuringPostMovements()) {
                    movementText = "Perform a movement after regular move of the 'react' or Pass";

                    // Get embark action for the reacting card
                    Action embarkAction = reactingCard.getBlueprint().getEmbarkAction(reactingCard.getOwner(), game, reactingCard, true, Filters.any);
                    if (embarkAction != null) {
                        actions.add(embarkAction);
                    }

                    // Get any disembark actions for other cards aboard the reacting card
                    Collection<PhysicalCard> cardsAboardReactingCard = Filters.filterActive(game, null,
                            Filters.and(Filters.your(reactingCard), Filters.aboardExceptRelatedSites(reactingCard), Filters.notPreventedFromParticipatingInReact));

                    for (PhysicalCard otherCard : cardsAboardReactingCard) {
                        Action disembarkAction = otherCard.getBlueprint().getDisembarkAction(otherCard.getOwner(), game, otherCard, true, false, Filters.any);
                        if (disembarkAction != null) {
                            actions.add(disembarkAction);
                        }
                    }
                }

                if (!actions.isEmpty()) {
                    // Ask the player to choose action to perform
                    game.getUserFeedback().sendAwaitingDecision(_action.getPerformingPlayer(),
                            new CardActionSelectionDecision(1, movementText, actions, _action.getPerformingPlayer().equals(gameState.getCurrentPlayerId()), false, isPassNotAllowed, false, false) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    final Action action = getSelectedAction(result);
                                    if (action != null) {
                                        subAction.appendEffect(
                                                new StackActionEffect(subAction, action));
                                        subAction.appendEffect(
                                                getNextSubActionEffect(subAction));
                                    }
                                }
                            }
                    );
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
