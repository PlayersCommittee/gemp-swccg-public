package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.PodraceFinishedResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An effect that finishes the current Podrace.
 */
public class FinishPodraceEffect extends AbstractSubActionEffect {
    private int _forceRetrievedByWinner;
    private int _forceLostByLoser;
    private boolean _takeRetrievedForceIntoHand;

    /**
     * Creates an effect that finishes the current Podrace
     * @param action the action performing this effect
     * @param forceRetrievedByWinner the amount of Force retrieved by winner
     * @param forceRetrievedByWinner the amount of Force lost by loser
     */
    public FinishPodraceEffect(Action action, int forceRetrievedByWinner, int forceLostByLoser, boolean takeRetrievedForceIntoHand) {
        super(action);
        _forceRetrievedByWinner = forceRetrievedByWinner;
        _forceLostByLoser = forceLostByLoser;
        _takeRetrievedForceIntoHand = takeRetrievedForceIntoHand;
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

        // Set winner loser
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Set Podrace has finishing
                        gameState.setPodraceFinishing();

                        // Set winner/loser
                        float darkRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, game.getDarkPlayer());
                        float lightRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, game.getLightPlayer());
                        if (darkRaceTotal > lightRaceTotal) {
                            gameState.setPodraceWinner(game.getDarkPlayer());
                            gameState.setPodraceLoser(game.getLightPlayer());
                            gameState.setPodraceWinnerRaceTotal(darkRaceTotal);
                            gameState.setPodraceLoserRaceTotal(lightRaceTotal);
                        }
                        else {
                            gameState.setPodraceWinner(game.getLightPlayer());
                            gameState.setPodraceLoser(game.getDarkPlayer());
                            gameState.setPodraceWinnerRaceTotal(lightRaceTotal);
                            gameState.setPodraceLoserRaceTotal(darkRaceTotal);
                        }

                        // Force retrieval/loss for winner/loser
                        SubAction retrieveOrLoseForceAction = new SubAction(subAction);
                        List<StandardEffect> effectsToOrder = new ArrayList<>();
                        float winnersForceRetrieval = modifiersQuerying.getPodraceForceRetrieval(gameState, _forceRetrievedByWinner);
                        if (winnersForceRetrieval > 0) {
                            effectsToOrder.add(new RetrieveForceEffect(retrieveOrLoseForceAction, gameState.getPodraceWinner(), winnersForceRetrieval) {
                                @Override
                                public boolean mayBeTakenIntoHand() {
                                    return _takeRetrievedForceIntoHand;
                                }
                            });

                        }
                        float losersForceLoss = modifiersQuerying.getPodraceForceLoss(gameState, _forceLostByLoser);
                        if (losersForceLoss > 0) {
                            effectsToOrder.add(new LoseForceEffect(retrieveOrLoseForceAction, gameState.getPodraceLoser(), losersForceLoss));
                        }
                        if (!effectsToOrder.isEmpty()) {
                            retrieveOrLoseForceAction.appendEffect(
                                    new ChooseEffectOrderEffect(retrieveOrLoseForceAction, effectsToOrder));
                        }

                        // Stack sub-action
                        subAction.stackSubAction(retrieveOrLoseForceAction);
                    }
                }
        );

        // Place all race destinies in owner's Used Piles and all Podracers are lost
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String winner = gameState.getPodraceWinner();
                        String loser = gameState.getPodraceLoser();
                        Collection<PhysicalCard> winnersRaceDestinies = Filters.filterStacked(game, Filters.and(Filters.raceDestiny, Filters.owner(winner)));
                        Collection<PhysicalCard> losersRaceDestinies = Filters.filterStacked(game, Filters.and(Filters.raceDestiny, Filters.owner(loser)));
                        Collection<PhysicalCard> winnersPodracers = Filters.filterAllOnTable(game, Filters.and(Filters.Podracer, Filters.owner(winner)));
                        Collection<PhysicalCard> losersPodracers = Filters.filterAllOnTable(game, Filters.and(Filters.Podracer, Filters.owner(loser)));

                        SubAction raceDestinyAndPodraceAction = new SubAction(subAction);

                        // Place all race destinies to place in Used Piles
                        raceDestinyAndPodraceAction.appendEffect(
                                new PutStackedCardsInUsedPileEffect(raceDestinyAndPodraceAction, winner, winnersRaceDestinies, false));
                        raceDestinyAndPodraceAction.appendEffect(
                                new PutStackedCardsInUsedPileEffect(raceDestinyAndPodraceAction, loser, losersRaceDestinies, false));

                        // All Podracers are lost
                        raceDestinyAndPodraceAction.appendEffect(
                                new LoseCardsFromTableEffect(raceDestinyAndPodraceAction, winnersPodracers));
                        raceDestinyAndPodraceAction.appendEffect(
                                new LoseCardsFromTableEffect(raceDestinyAndPodraceAction, losersPodracers));

                        // Stack sub-action
                        subAction.stackSubAction(raceDestinyAndPodraceAction);
                    }
                }
        );

        // Podrace finished
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.setPodraceFinished();
                        game.getActionsEnvironment().emitEffectResult(new PodraceFinishedResult(_action));
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
