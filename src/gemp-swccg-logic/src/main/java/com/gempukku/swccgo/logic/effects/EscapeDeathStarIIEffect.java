package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that allows Light side starfighters to attempt to 'escape' from Death Star II when it is being 'blown away'.
 */
public class EscapeDeathStarIIEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that allows Light side starfighters to attempt to 'escape' from Death Star II when it is being 'blown away'.
     * @param action the action performing this effect
     */
    public EscapeDeathStarIIEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final String lightSidePlayerId = game.getLightPlayer();
        final String darkSidePlayerId = game.getDarkPlayer();
        final Filter lightSideFilter = Filters.movableToEscapeDeathStarII(lightSidePlayerId);
        final Filter darkSideFilter = Filters.movableToEscapeDeathStarII(darkSidePlayerId);

        final SubAction subAction = new SubAction(_action);

        // First, Light side player may attempt to move any of that player's starships at Reactor Core
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(lightSideFilter, Filters.at(Filters.Reactor_Core));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction lightSideEscapeSubAction = new SubAction(subAction, lightSidePlayerId);
                            lightSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(lightSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(lightSideEscapeSubAction);
                        }
                    }
                }
        );

        // Next, Dark side player may attempt to move any of that player's starships at Reactor Core
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(darkSideFilter, Filters.at(Filters.Reactor_Core));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction darkSideEscapeSubAction = new SubAction(subAction, darkSidePlayerId);
                            darkSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(darkSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(darkSideEscapeSubAction);
                        }
                    }
                }
        );

        // Next, Light side player may attempt to move any of that player's starships at Capacitors
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(lightSideFilter, Filters.at(Filters.Capacitors));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction lightSideEscapeSubAction = new SubAction(subAction, lightSidePlayerId);
                            lightSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(lightSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(lightSideEscapeSubAction);
                        }
                    }
                }
        );

        // Next, Dark side player may attempt to move any of that player's starships at Capacitors
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(darkSideFilter, Filters.at(Filters.Capacitors));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction darkSideEscapeSubAction = new SubAction(subAction, darkSidePlayerId);
                            darkSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(darkSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(darkSideEscapeSubAction);
                        }
                    }
                }
        );

        // Next, Light side player may attempt to move any of that player's starships at Coolant Shaft
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(lightSideFilter, Filters.at(Filters.Coolant_Shaft));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction lightSideEscapeSubAction = new SubAction(subAction, lightSidePlayerId);
                            lightSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(lightSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(lightSideEscapeSubAction);
                        }
                    }
                }
        );

        // Next, Dark side player may attempt to move any of that player's starships at Coolant Shaft
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter filter = Filters.and(darkSideFilter, Filters.at(Filters.Coolant_Shaft));
                        if (Filters.canSpot(game, null, filter)) {

                            SubAction darkSideEscapeSubAction = new SubAction(subAction, darkSidePlayerId);
                            darkSideEscapeSubAction.appendEffect(
                                    new ChooseNextCardToMove(darkSideEscapeSubAction, game, filter));
                            subAction.stackSubAction(darkSideEscapeSubAction);
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

    /**
     * A private effect for choosing the next card to move to 'escape' Death Star II.
     */
    private class ChooseNextCardToMove extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Filterable _cardFilter;

        /**
         * Creates an effect for choosing the next card to move to 'escape' Death Star II.
         * @param subAction the action
         * @param game the game
         * @param cardFilter the card filter
         */
        public ChooseNextCardToMove(SubAction subAction, SwccgGame game, Filterable cardFilter) {
            super(subAction, subAction.getPerformingPlayer(), "Choose starship to move, or click 'Done'", 0, 1, cardFilter);
            _subAction = subAction;
            _game = game;
            _cardFilter = cardFilter;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out moving card
                SubAction moveCardSubAction = new SubAction(_subAction);
                Action moveCardAction = selectedCard.getBlueprint().getMoveUsingEscapeFromDeathStarIIMovementAction(selectedCard.getOwner(), _game, selectedCard);
                if (moveCardAction != null) {
                    moveCardSubAction.appendEffect(
                            new StackActionEffect(moveCardSubAction, moveCardAction));
                }
                // Stack sub-action
                _subAction.stackSubAction(moveCardSubAction);

                _subAction.appendEffect(
                        new PassthruEffect(_subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (Filters.canSpot(_game, null, _cardFilter)) {
                                    _subAction.appendEffect(
                                            new ChooseNextCardToMove(_subAction, game, _cardFilter));
                                }
                            }
                        }
                );
            }
        }
    }
}
