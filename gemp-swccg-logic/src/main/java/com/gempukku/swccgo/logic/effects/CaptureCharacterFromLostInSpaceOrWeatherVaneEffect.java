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
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An effect that carries out the capturing of a single character from an Effect (such as Lost In Space or Weather Vane).
 */
public class CaptureCharacterFromLostInSpaceOrWeatherVaneEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _characterToCapture;
    private PhysicalCard _location;

    /**
     * Creates an effect that carries out the capturing of a single character from an Effect (such as Lost In Space or Weather Vane).
     * @param action the action performing this effect
     * @param characterToCapture the character to capture
     * @param location the location at which to capture character
     */
    public CaptureCharacterFromLostInSpaceOrWeatherVaneEffect(Action action, PhysicalCard characterToCapture, PhysicalCard location) {
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

                                                                        //resets modifiers
                                                                        _characterToCapture.stopAffectingGame();
                                                                        _characterToCapture.startAffectingGame(game);

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

                                                        //resets modifiers
                                                        _characterToCapture.stopAffectingGame();
                                                        _characterToCapture.startAffectingGame(game);

                                                        // Emit effect result that character was captured
                                                        game.getActionsEnvironment().emitEffectResult(new CaptureCharacterResult(_performingPlayerId, subAction.getActionSource(), null, _characterToCapture, false, false, CaptureOption.IMPRISONMENT));
                                                    }
                                                    else {
                                                        // Capture with 'Escape'
                                                        subAction.appendEffect(
                                                                new CaptureWithEscapeEffect(subAction, _characterToCapture, false, false, null));

                                                        //resets modifiers
                                                        _characterToCapture.stopAffectingGame();
                                                        _characterToCapture.startAffectingGame(game);
                                                    }
                                                }
                                            }));
                        }
                        else {
                            // Capture with 'Escape' is only option
                            subAction.appendEffect(
                                    new CaptureWithEscapeEffect(subAction, _characterToCapture, false, false, null));
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
