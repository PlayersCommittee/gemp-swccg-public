package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DeployAsReactState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.MoveAsReactState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.MayNotReactModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;

/**
 * An effect that cancels the current 'react'.
 */
public class CancelReactEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that cancels the current 'react'.
     * @param action the action performing this effect
     */
    public CancelReactEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final PhysicalCard source = _action.getActionSource();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Deploy as 'react'
                        DeployAsReactState deployAsReactState = gameState.getDeployAsReactState();
                        if (deployAsReactState != null) {
                            RespondableDeployAsReactEffect effect = deployAsReactState.getRespondableEffect();
                            if (!effect.isCanceled()) {

                                // Cancel the 'react'
                                gameState.sendMessage(_action.getPerformingPlayer() + " cancels 'react' using " + GameUtils.getCardLink(source));
                                effect.cancel(source);
                                gameState.finishDeployAsReact();

                                // Prevent cards involved in the 'react' (and cards with same title) from participating in another react this turn
                                game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                                        new MayNotReactModifier(null, Filters.title(effect.getCard1().getTitle())));
                                if (effect.getCard2() != null) {
                                    game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                                            new MayNotReactModifier(null, Filters.title(effect.getCard2().getTitle())));
                                }

                                PhysicalCard card1 = effect.getCard1();
                                PhysicalCard stackedOn1 = effect.getFromStackedOn1();
                                Zone zone1 = effect.getFromZone1();

                                // Put the cards being deployed back where they came from
                                gameState.removeCardsFromZone(Collections.singleton(card1));
                                if (stackedOn1 != null) {
                                    gameState.stackCard(card1, stackedOn1, false, card1.isStackedAsInactive(), false);
                                }
                                else {
                                    gameState.addCardToZone(card1, zone1, card1.getOwner());
                                }

                                PhysicalCard card2 = effect.getCard2();
                                PhysicalCard stackedOn2 = effect.getFromStackedOn2();
                                Zone zone2 = effect.getFromZone2();

                                if (card2 != null) {
                                    gameState.removeCardsFromZone(Collections.singleton(card2));
                                    if (stackedOn2 != null) {
                                        gameState.stackCard(card2, stackedOn2, false, card2.isStackedAsInactive(), false);
                                    }
                                    else {
                                        gameState.addCardToZone(card2, zone2, card2.getOwner());
                                    }
                                }

                                if (zone1.isCardPile()) {
                                    subAction.appendEffect(
                                            new ShufflePileEffect(subAction, null, card1.getOwner(), card1.getOwner(), zone1, true));
                                }
                                if (card2 != null && zone2 != null && zone2 != zone1 && zone2.isCardPile()) {
                                    subAction.appendEffect(
                                            new ShufflePileEffect(subAction, null, card2.getOwner(), card2.getOwner(), zone2, true));
                                }
                            }
                        }
                        else {
                            // Move as 'react'
                            MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();
                            if (moveAsReactState != null) {
                                if (moveAsReactState.canContinue()) {

                                    // Cancel the 'react'
                                    gameState.sendMessage(_action.getPerformingPlayer() + " cancels 'react' using " + GameUtils.getCardLink(source));
                                    moveAsReactState.cancel(source);

                                    // Prevent cards involved in the 'react' from participating in another react this turn
                                    for (PhysicalCard cardInReact : moveAsReactState.getCardsParticipatingInReact()) {
                                        game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                                                new MayNotReactModifier(null, cardInReact));
                                    }
                                }
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
