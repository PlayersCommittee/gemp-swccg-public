package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified player to choose a 'bluff card' to flip over.
 */
public class FlipOverBluffCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;

    /**
     * Creates an effect that causes the specified player to choose a 'bluff card' to flip over.
     * @param action the action performing this effect
     * @param location the location the 'bluff cards' are stacked on
     */
    public FlipOverBluffCardEffect(Action action, PhysicalCard location) {
        super(action);
        _location = location;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new ChooseStackedCardEffect(subAction, _action.getPerformingPlayer(), _location, Filters.bluffCard, true) {
                    @Override
                    protected void cardSelected(final PhysicalCard selectedCard) {
                        // Flip over 'bluff card'
                        game.getGameState().flipCard(game, selectedCard, false);
                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " flips over 'bluff card' " + GameUtils.getCardLink(selectedCard));

                        // Check if 'bluff card' is a character or vehicle that can deploy for free to the location
                        boolean deployable = Filters.and(Filters.or(Filters.character, Filters.vehicle),
                                Filters.deployableToLocation(_action.getActionSource(), Filters.sameCardId(_location), true, 0)).accepts(game.getGameState(), game.getModifiersQuerying(), selectedCard);

                        if (deployable) {
                            // Ask player to deploy card
                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, selectedCard.getOwner(),
                                            new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(selectedCard) + " for free to " + GameUtils.getCardLink(_location) + "?") {
                                                @Override
                                                protected void yes() {
                                                    // Deploy card for free to location
                                                    subAction.appendEffect(
                                                            new StackActionEffect(subAction, selectedCard.getBlueprint().getPlayCardAction(selectedCard.getOwner(), game, selectedCard,
                                                                    _action.getActionSource(), true, 0, null, null, null, null, null, false, 0, Filters.locationAndCardsAtLocation(Filters.sameCardId(_location)), null)));
                                                }
                                                @Override
                                                protected void no() {
                                                    game.getGameState().sendMessage(selectedCard.getOwner() + " chooses to not deploy " + GameUtils.getCardLink(selectedCard) + " to " + GameUtils.getCardLink(_location));
                                                    // Lose 2 Force and 'bluff card'
                                                    subAction.appendEffect(
                                                            new LoseForceEffect(subAction, selectedCard.getOwner(), 2));
                                                    subAction.appendEffect(
                                                            new LoseCardsFromTableSimultaneouslyEffect(subAction, Collections.singleton(selectedCard), true, false));
                                                }
                                            }
                                    ));
                        }
                        else {
                            game.getGameState().sendMessage(selectedCard.getOwner() + " is not able to deploy " + GameUtils.getCardLink(selectedCard) + " to " + GameUtils.getCardLink(_location));
                            // Lose 2 Force and 'bluff card'
                            subAction.appendEffect(
                                    new LoseForceEffect(subAction, selectedCard.getOwner(), 2));
                            subAction.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(subAction, Collections.singleton(selectedCard), true, false));
                        }
                    }
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose 'bluff card' to flip over";
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
