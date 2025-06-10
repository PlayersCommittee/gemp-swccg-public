package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * An effect that carries out the capturing of a single "just lost" character from Lost Pile.
 */
public class CaptureCharacterFromLostPileEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _characterToCapture;
    private PhysicalCard _location;

    /**
     * Creates an effect that carries out the capturing of a single "just lost" character from Lost Pile.
     * @param action the action performing this effect
     * @param characterToCapture the character to capture
     * @param location the location at which to capture character
     */
    public CaptureCharacterFromLostPileEffect(Action action, PhysicalCard characterToCapture, PhysicalCard location) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _characterToCapture = characterToCapture;
        _location = location;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        //
        // 1) Determine which options are available: 'Seizure', 'Imprisonment', or 'Escape'
        //
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {

                        // Check if 'Seizure' is a valid option
                        final Collection<PhysicalCard> validToSeizeCaptive = Filters.filterActive(game, null,
                                Filters.and(Filters.owner(_performingPlayerId), Filters.canEscortCaptive(_characterToCapture), Filters.at(_location)));

                        // Check if 'Imprisonment' is a valid option
                        boolean isAtPrison = Filters.prison.accepts(game.getGameState(), game.getModifiersQuerying(), _location);

                        if (!validToSeizeCaptive.isEmpty() || isAtPrison) {
                            List<String> choices = new ArrayList<String>();
                            if (!validToSeizeCaptive.isEmpty()) choices.add(CaptureOption.SEIZE.getHumanReadable());
                            if (isAtPrison) choices.add(CaptureOption.IMPRISONMENT.getHumanReadable());
                            choices.add(CaptureOption.ESCAPE.getHumanReadable());
                            String[] choiceArray = new String[choices.size()];
                            choices.toArray(choiceArray);

                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                            new MultipleChoiceAwaitingDecision("Choose option for capturing " + GameUtils.getCardLink(_characterToCapture), choiceArray) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (result.equals(CaptureOption.SEIZE.getHumanReadable())) {

                                                        // Need to choose character that will "seize" the captive
                                                        subAction.appendEffect(
                                                                new ChooseCardOnTableEffect(subAction, _performingPlayerId, "Choose escort for " + GameUtils.getCardLink(_characterToCapture), validToSeizeCaptive) {
                                                                    @Override
                                                                    protected void cardSelected(PhysicalCard escort) {
                                                                        // Capture with 'Seizure'
                                                                        StringBuilder msgText = new StringBuilder(_performingPlayerId).append(" causes ").append(GameUtils.getCardLink(_characterToCapture)).append(" to be captured");
                                                                        msgText.append(" and 'seized' by ").append(GameUtils.getCardLink(escort)).append(" using ").append(GameUtils.getCardLink(_action.getActionSource()));
                                                                        gameState.sendMessage(msgText.toString());
                                                                        gameState.seizeCharacter(game, _characterToCapture, escort);
                                                                        attachPreviouslyAttachedCardsStillInLostPile(gameState, modifiersQuerying, _characterToCapture);

                                                                        // Emit effect result that character was captured
                                                                        game.getActionsEnvironment().emitEffectResult(new CaptureCharacterResult(_performingPlayerId, subAction.getActionSource(), null, _characterToCapture, false, false, CaptureOption.SEIZE));
                                                                    }
                                                                });
                                                    }
                                                    else if (result.equals(CaptureOption.IMPRISONMENT.getHumanReadable())) {
                                                        // Capture with 'Imprisonment'
                                                        StringBuilder msgText = new StringBuilder(_performingPlayerId).append(" causes ").append(GameUtils.getCardLink(_characterToCapture)).append(" to be captured");
                                                        msgText.append(" and 'imprisoned' in ").append(GameUtils.getCardLink(_location)).append(" using ").append(GameUtils.getCardLink(_action.getActionSource()));
                                                        gameState.sendMessage(msgText.toString());
                                                        gameState.imprisonCharacter(game, _characterToCapture, _location);
                                                        attachPreviouslyAttachedCardsStillInLostPile(gameState, modifiersQuerying, _characterToCapture);

                                                        // Emit effect result that character was captured
                                                        game.getActionsEnvironment().emitEffectResult(new CaptureCharacterResult(_performingPlayerId, subAction.getActionSource(), null, _characterToCapture, false, false, CaptureOption.IMPRISONMENT));
                                                    }
                                                    else {
                                                        // Capture with 'Escape'
                                                        StringBuilder msgText = new StringBuilder(_performingPlayerId).append(" causes ").append(GameUtils.getCardLink(_characterToCapture));
                                                        msgText.append(" to be captured and allowed to 'escape' using using ").append(GameUtils.getCardLink(_action.getActionSource()));
                                                        gameState.sendMessage(msgText.toString());
                                                        subAction.appendEffect(
                                                                new PutCardFromLostPileInUsedPileEffect(subAction, _performingPlayerId, _characterToCapture, false, true));
                                                    }
                                                }
                                            }));
                        }
                        else {
                            // Capture with 'Escape' is only option
                            StringBuilder msgText = new StringBuilder(_performingPlayerId).append(" causes ").append(GameUtils.getCardLink(_characterToCapture));
                            msgText.append(" to be captured and allowed to 'escape' using using ").append(GameUtils.getCardLink(_action.getActionSource()));
                            gameState.sendMessage(msgText.toString());
                            subAction.appendEffect(
                                    new PutCardFromLostPileInUsedPileEffect(subAction, _performingPlayerId, _characterToCapture, false, true));
                        }
                    }
                }
        );
        return subAction;
    }

    /**
     * Attaches the cards in either player's Lost Pile that were previously attached to the specified card when it was removed from the table.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the card
     */
    private void attachPreviouslyAttachedCardsStillInLostPile(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        Collection<PhysicalCard> prevAttachedCards = Filters.filter(card.getCardsPreviouslyAttached(), gameState.getGame(), Filters.inLostPile);
        for (PhysicalCard prevAttachedCard : prevAttachedCards) {
            gameState.removeCardsFromZone(Collections.singleton(prevAttachedCard));
            gameState.attachCard(prevAttachedCard, card);
            attachPreviouslyAttachedCardsStillInLostPile(gameState, modifiersQuerying, prevAttachedCard);
        }
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
