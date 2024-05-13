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
 * An effect that allows starfighters to move out of Trench during an Attack Run.
 */
public class PullUpDuringAttackRunEffect extends AbstractSubActionEffect {
    private Filter _lightSideStarfighterFilter;
    private Filter _darkSideStarfighterFilter;
    private PhysicalCard _attackRun;


    /**
     * Creates an effect that allows starfighters to move out of Trench during an Attack Run.
     * @param action the action performing this effect
     */
    public PullUpDuringAttackRunEffect(Action action) {
        super(action);
        _attackRun = action.getActionSource();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final String lightSidePlayerId = game.getLightPlayer();
        final String darkSidePlayerId = game.getDarkPlayer();
        _lightSideStarfighterFilter = Filters.and(Filters.owner(lightSidePlayerId), Filters.starfighter, Filters.canMoveAtEndOfAttackRun(lightSidePlayerId));
        _darkSideStarfighterFilter = Filters.and(Filters.owner(darkSidePlayerId), Filters.starfighter, Filters.canMoveAtEndOfAttackRun(darkSidePlayerId));

        final SubAction subAction = new SubAction(_action);

        // First, Light side player moves that player's starfighters out of trench.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (Filters.canSpot(game, _attackRun, _lightSideStarfighterFilter)) {

                            SubAction lightSideMoveSubAction = new SubAction(subAction, lightSidePlayerId);
                            lightSideMoveSubAction.appendEffect(
                                    new ChooseNextCardToMove(lightSideMoveSubAction, game, _lightSideStarfighterFilter));
                            subAction.stackSubAction(lightSideMoveSubAction);
                        }
                    }
                }
        );

        // Then, Dark side player moves that player's starfighters out of trench.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (Filters.canSpot(game, _attackRun, _darkSideStarfighterFilter)) {

                            SubAction darkSideMoveSubAction = new SubAction(subAction, darkSidePlayerId);
                            darkSideMoveSubAction.appendEffect(
                                    new ChooseNextCardToMove(darkSideMoveSubAction, game, _darkSideStarfighterFilter));
                            subAction.stackSubAction(darkSideMoveSubAction);
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
     * A private effect for choosing the next card to move out of Death Star: Trench.
     */
    private class ChooseNextCardToMove extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private String _playerId;

        /**
         * Creates an effect for choosing the next card to move out of Death Star: Trench.
         * @param subAction the action
         * @param game the game
         * @param cardFilter the card filter
         */
        public ChooseNextCardToMove(SubAction subAction, SwccgGame game, Filterable cardFilter) {
            super(subAction, subAction.getPerformingPlayer(), "Choose starfighter to move out of trench", 1, 1, cardFilter);
            _subAction = subAction;
            _game = game;
            _playerId = subAction.getPerformingPlayer();
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out moving card
                SubAction moveCardSubAction = new SubAction(_subAction);
                Action moveCardAction = selectedCard.getBlueprint().getMoveAtEndOfAttackRunAction(selectedCard.getOwner(), _game, selectedCard);
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
                                if (_playerId.equals(game.getLightPlayer())) {
                                    if (Filters.canSpot(_game, _attackRun, _lightSideStarfighterFilter)) {
                                        _subAction.appendEffect(
                                                new PullUpDuringAttackRunEffect.ChooseNextCardToMove(_subAction, game, _lightSideStarfighterFilter));
                                    }
                                }
                                else {
                                    if (Filters.canSpot(_game, _attackRun, _darkSideStarfighterFilter)) {
                                        _subAction.appendEffect(
                                                new PullUpDuringAttackRunEffect.ChooseNextCardToMove(_subAction, game, _darkSideStarfighterFilter));
                                    }
                                }
                            }
                        }
                );
            }
        }
    }
}
