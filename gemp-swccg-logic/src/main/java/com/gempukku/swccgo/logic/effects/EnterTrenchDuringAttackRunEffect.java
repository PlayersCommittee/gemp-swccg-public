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
 * An effect that allows starfighters to move into Trench during an Attack Run.
 */
public class EnterTrenchDuringAttackRunEffect extends AbstractSubActionEffect {
    private int _numLightSideEnteredTrench;
    private int _numDarkSideEnteredTrench;
    private Filter _lightSideStarfighterToMoveIntoTrench;
    private Filter _darkSideTIEToMoveIntoTrench;
    private PhysicalCard _attackRun;


    /**
     * Creates an effect that allows starfighters to move into Trench during an Attack Run.
     * @param action the action performing this effect
     */
    public EnterTrenchDuringAttackRunEffect(Action action) {
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
        _lightSideStarfighterToMoveIntoTrench = Filters.and(Filters.owner(lightSidePlayerId), Filters.starfighter, Filters.canMoveAtStartOfAttackRun(lightSidePlayerId));
        _darkSideTIEToMoveIntoTrench = Filters.and(Filters.owner(darkSidePlayerId), Filters.TIE, Filters.canMoveAtStartOfAttackRun(darkSidePlayerId));

        final SubAction subAction = new SubAction(_action);

        // First, Light side player may move up to 3 starfighters into trench. A lead starfighter is required.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter validLeadStarfighter = Filters.and(_lightSideStarfighterToMoveIntoTrench, Filters.hasAttached(Filters.Proton_Torpedoes));
                        if (Filters.canSpot(game, _attackRun, validLeadStarfighter)) {

                            SubAction lightSideMoveSubAction = new SubAction(subAction, lightSidePlayerId);
                            lightSideMoveSubAction.appendEffect(
                                    new ChooseNextCardToMove(lightSideMoveSubAction, game, true, validLeadStarfighter));
                            subAction.stackSubAction(lightSideMoveSubAction);
                        }
                    }
                }
        );

        // Then, Dark side player may move up to 3 TIEs into trench.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_numLightSideEnteredTrench > 0
                                && Filters.canSpot(game, _attackRun, _darkSideTIEToMoveIntoTrench)) {

                            SubAction darkSideMoveSubAction = new SubAction(subAction, darkSidePlayerId);
                            darkSideMoveSubAction.appendEffect(
                                    new ChooseNextCardToMove(darkSideMoveSubAction, game, false, Filters.and(_darkSideTIEToMoveIntoTrench, Filters.tieCountNoMoreThan(3 - _numDarkSideEnteredTrench))));
                            subAction.stackSubAction(darkSideMoveSubAction);
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _numLightSideEnteredTrench > 0;
    }

    /**
     * A private effect for choosing the next card to move into Death Star: Trench.
     */
    private class ChooseNextCardToMove extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private String _playerId;

        /**
         * Creates an effect for choosing the next card to move into Death Star: Trench.
         * @param subAction the action
         * @param game the game
         * @param cardFilter the card filter
         */
        public ChooseNextCardToMove(SubAction subAction, SwccgGame game, boolean required, Filterable cardFilter) {
            super(subAction, subAction.getPerformingPlayer(), "Choose starfighter to move into trench" + (!required ? ", or click 'Done'" : ""), required ? 1 : 0, 1, cardFilter);
            _subAction = subAction;
            _game = game;
            _playerId = subAction.getPerformingPlayer();
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out moving card
                SubAction moveCardSubAction = new SubAction(_subAction);
                Action moveCardAction = selectedCard.getBlueprint().getMoveAtStartOfAttackRunAction(selectedCard.getOwner(), _game, selectedCard);
                if (moveCardAction != null) {
                    moveCardSubAction.appendEffect(
                            new StackActionEffect(moveCardSubAction, moveCardAction));
                }
                final int numToIncrement = _playerId.equals(_game.getLightPlayer()) ? _lightSideStarfighterToMoveIntoTrench.acceptsCount(_game, selectedCard) : _darkSideTIEToMoveIntoTrench.acceptsCount(_game, selectedCard);
                // Stack sub-action
                _subAction.stackSubAction(moveCardSubAction);

                _subAction.appendEffect(
                        new PassthruEffect(_subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (_playerId.equals(game.getLightPlayer())) {
                                    _numLightSideEnteredTrench += numToIncrement;
                                    if (_numLightSideEnteredTrench < 3
                                            && Filters.canSpot(_game, _attackRun, _lightSideStarfighterToMoveIntoTrench)) {
                                        _subAction.appendEffect(
                                                new EnterTrenchDuringAttackRunEffect.ChooseNextCardToMove(_subAction, game, false, _lightSideStarfighterToMoveIntoTrench));
                                    }
                                }
                                else {
                                    _numDarkSideEnteredTrench += numToIncrement;
                                    if (_numDarkSideEnteredTrench < 3
                                            && Filters.canSpot(_game, _attackRun, Filters.and(_darkSideTIEToMoveIntoTrench, Filters.tieCountNoMoreThan(3 - _numDarkSideEnteredTrench)))) {
                                        _subAction.appendEffect(
                                                new EnterTrenchDuringAttackRunEffect.ChooseNextCardToMove(_subAction, game, false, Filters.and(_darkSideTIEToMoveIntoTrench, Filters.tieCountNoMoreThan(3 - _numDarkSideEnteredTrench))));
                                    }
                                }
                            }
                        }
                );
            }
        }
    }
}
