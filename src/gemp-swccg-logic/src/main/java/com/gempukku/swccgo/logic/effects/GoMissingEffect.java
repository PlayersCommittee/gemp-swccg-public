package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.GoneMissingResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes specified characters to go 'missing'.
 */
public class GoMissingEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _charactersToGoMissing;

    /**
     * Creates an effect that causes a character to go 'missing'.
     * @param action the action performing this effect
     * @param characterToGoMissing the character to go 'missing'
     */
    public GoMissingEffect(Action action, PhysicalCard characterToGoMissing) {
        this(action, Collections.singleton(characterToGoMissing));
    }

    /**
     * Creates an effect that causes characters to go 'missing'.
     * @param action the action performing this effect
     * @param charactersToGoMissing the characters to go 'missing'
     */
    public GoMissingEffect(Action action, Collection<PhysicalCard> charactersToGoMissing) {
        super(action);
        _charactersToGoMissing = charactersToGoMissing;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final String performingPlayerId = _action.getPerformingPlayer();
        final PhysicalCard source = _action.getActionSource();

        final SubAction subAction = new SubAction(_action);

        final Collection<PhysicalCard> charactersToGoMissing = Filters.filter(_charactersToGoMissing, game, Filters.not(Filters.missing));
        if (!charactersToGoMissing.isEmpty()) {

            // Special Rule for Luke's Backpack (if character carrying it goes missing, then character in it also goes missing, and vice versa)
            if (Filters.canSpot(charactersToGoMissing, game, Filters.and(Filters.character, Filters.hasAttached(Filters.Lukes_Backpack)))) {
                charactersToGoMissing.addAll(Filters.filterAllOnTable(game, Filters.and(Filters.not(Filters.in(charactersToGoMissing)), Filters.character, Filters.attachedTo(Filters.Lukes_Backpack))));
            }
            else if (Filters.canSpot(charactersToGoMissing, game, Filters.and(Filters.character, Filters.attachedTo(Filters.Lukes_Backpack)))) {
                charactersToGoMissing.addAll(Filters.filterAllOnTable(game, Filters.and(Filters.not(Filters.in(charactersToGoMissing)), Filters.character, Filters.hasAttached(Filters.Lukes_Backpack))));
            }

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            if (performingPlayerId != null)
                                gameState.sendMessage(performingPlayerId + " causes " + GameUtils.getAppendedNames(charactersToGoMissing) + " to go 'missing' using " + GameUtils.getCardLink(source));
                            else
                                gameState.sendMessage(GameUtils.getCardLink(source) + " causes " + GameUtils.getAppendedNames(charactersToGoMissing) + " to go 'missing'");

                            gameState.cardAffectsCards(performingPlayerId, source, charactersToGoMissing);

                            final Collection<PhysicalCard> captivesGoneMissing = Filters.filter(charactersToGoMissing, game, Filters.captive);

                            // Set the cards as missing (also disembarks and frees if needed).
                            for (PhysicalCard character : charactersToGoMissing) {
                                gameState.makeGoMissing(game, character);

                                if (captivesGoneMissing.contains(character)) {
                                    game.getModifiersEnvironment().removeEndOfCaptivity(character);
                                }
                            }

                            // Release any captives the missing escorts have (that were not already released by themselves going missing).
                            List<PhysicalCard> captivesToRelease = new LinkedList<PhysicalCard>();
                            for (PhysicalCard character : charactersToGoMissing) {
                                captivesToRelease.addAll(gameState.getCaptivesOfEscort(character));
                            }
                            if (!captivesToRelease.isEmpty()) {
                                subAction.appendEffect(
                                        new ReleaseCaptivesEffect(subAction, captivesToRelease, true));
                            }

                            // Emit effect result that cards went missing
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            for (PhysicalCard character : charactersToGoMissing) {
                                                game.getActionsEnvironment().emitEffectResult(
                                                        new GoneMissingResult(performingPlayerId, character, captivesGoneMissing.contains(character)));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
