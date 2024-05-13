package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

import java.util.Collection;

/**
 * The game process for playing starting effects.
 */
public class PlayStartingEffectsGameProcess implements GameProcess {
    private boolean _readyToBegin;
    private String _darkPlayerId;
    private String _lightPlayerId;
    private GameProcess _nextProcess;

    /**
     * Creates the game process for playing starting effects.
     * @param game the game
     */
    public PlayStartingEffectsGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _nextProcess = this;
    }

    @Override
    public void process(final SwccgGame game) {
        if (!_readyToBegin) {
            StringBuilder msgTextBuilder = new StringBuilder();
            if (game.getFormat().hasJpSealedRule() || game.getFormat().hasDownloadBattlegroundRule()) {
                msgTextBuilder.append("Special Rules for this game:");
                msgTextBuilder.append("<ul>");
                if (game.getFormat().hasDownloadBattlegroundRule()) {
                    msgTextBuilder.append("<li>Once per game, may ▼ a unique battleground not already on table");
                    msgTextBuilder.append("</li>");
                }
                if (game.getFormat().hasJpSealedRule()) {
                    msgTextBuilder.append("<li>Any character of ability 1 who has a printed deploy number of 3 or greater is considered, for all purposes, to be ability 2");
                    msgTextBuilder.append("</li>");
                }
                msgTextBuilder.append("</ul>");
            }
            msgTextBuilder.append("Select OK to start game");
            String msgText = msgTextBuilder.toString();
            game.getUserFeedback().sendAwaitingDecision(_darkPlayerId,
                    new MultipleChoiceAwaitingDecision(msgText, new String[]{"OK"}) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                        }
                    }
            );
            game.getUserFeedback().sendAwaitingDecision(_lightPlayerId,
                    new MultipleChoiceAwaitingDecision(msgText, new String[]{"OK"}) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                        }
                    }
            );

            _readyToBegin = true;
            return;
        }

        // Play starting effects (Dark then Light).
        SystemQueueAction action = new SystemQueueAction();

        Collection<PhysicalCard> darkSideChoices = Filters.filter(game.getGameState().getReserveDeck(_darkPlayerId), game, Filters.Starting_Effect);
        if (!darkSideChoices.isEmpty()) {
            PhysicalCard darkSideStartingEffect = darkSideChoices.iterator().next();
            action.appendEffect(
                    new StackActionEffect(action, darkSideStartingEffect.getBlueprint().getPlayCardAction(_darkPlayerId, game, darkSideStartingEffect, darkSideStartingEffect, false, 0, null, null, null, null, null, false, 0, Filters.none, null)));
        }
        Collection<PhysicalCard> lightSideChoices = Filters.filter(game.getGameState().getReserveDeck(_lightPlayerId), game, Filters.Starting_Effect);
        if (!lightSideChoices.isEmpty()) {
            PhysicalCard lightSideStartingEffect = lightSideChoices.iterator().next();
            action.appendEffect(
                    new StackActionEffect(action, lightSideStartingEffect.getBlueprint().getPlayCardAction(_lightPlayerId, game, lightSideStartingEffect, lightSideStartingEffect, false, 0, null, null, null, null, null, false, 0, Filters.none, null)));
        }
        
        game.getActionsEnvironment().addActionToStack(action);

        _nextProcess = new PlayStartingLocationsAndObjectivesGameProcess(game);
    }

    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
