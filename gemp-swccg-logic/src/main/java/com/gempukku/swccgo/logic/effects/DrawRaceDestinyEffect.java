package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/**
 * An effect that performs drawing race destiny during a Podrace.
 */
public class DrawRaceDestinyEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that performs drawing race destiny during a Podrace.
     * @param action the action performing this effect
     */
    public DrawRaceDestinyEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        // 1) Choose the Podracer you are drawing destiny for (if multiple), otherwise automatically chooses Podracer, or Podracer Arena if no Podracer
                        Filter podracerFilter = Filters.and(Filters.owner(performingPlayerId), Filters.Podracer);
                        if (Filters.canSpot(game, null, podracerFilter)) {
                            subAction.appendEffect(
                                    new ChooseCardOnTableEffect(subAction, performingPlayerId, "Choose Podracer", podracerFilter) {
                                        @Override
                                        protected boolean getUseShortcut() {
                                            return true;
                                        }
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            drawRaceDestiny(game, subAction, selectedCard);
                                        }
                                    }
                            );
                        }
                        else {
                            PhysicalCard podraceArena = Filters.findFirstActive(game, null, Filters.Podrace_Arena);
                            if (podraceArena != null) {
                                drawRaceDestiny(game, subAction, podraceArena);
                            }
                        }
                    }
                });
        return subAction;
    }

    /**
     * Draws race destiny to be placed on the specified Podracer or Podracing Arena.
     * @param game the game
     * @param subAction the action
     * @param podracerOrArena the Podracer or Podracing Arena
     */
    private void drawRaceDestiny(SwccgGame game, SubAction subAction, final PhysicalCard podracerOrArena) {
        String performingPlayerId = subAction.getPerformingPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        int numDestinyToDraw = 1;
        int numDestinyToChoose = 1;
        if (Filters.Podracer.accepts(game, podracerOrArena)) {
            numDestinyToDraw = modifiersQuerying.getNumRaceDestinyToDraw(gameState, performingPlayerId, podracerOrArena);
            if (numDestinyToDraw > 1) {
                numDestinyToChoose = modifiersQuerying.getNumRaceDestinyToChoose(gameState, performingPlayerId, podracerOrArena);
            }
        }
        if (numDestinyToDraw == 0) {
            gameState.sendMessage(performingPlayerId + " draws no race destiny");
            return;
        }
        boolean isDrawXChooseY = numDestinyToChoose != 0 && (numDestinyToChoose < numDestinyToDraw);
        gameState.sendMessage(performingPlayerId + " targets " + GameUtils.getCardLink(podracerOrArena) + " to draw " + numDestinyToDraw
                + (isDrawXChooseY ? (" and choose " + numDestinyToChoose) : "") + " race destiny");
        gameState.cardAffectsCard(performingPlayerId, subAction.getActionAttachedToCard(), podracerOrArena);
        subAction.appendEffect(
                new DrawDestinyEffect(subAction, performingPlayerId, isDrawXChooseY ? 1 : numDestinyToDraw, isDrawXChooseY ? numDestinyToDraw : 0, isDrawXChooseY ? numDestinyToChoose : 0, false, DestinyType.RACE_DESTINY) {
                    @Override
                    public PhysicalCard getStackRaceDestinyOn() {
                        return podracerOrArena;
                    }
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                    }
                });
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
