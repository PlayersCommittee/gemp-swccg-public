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
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCaptureCardResult;

import java.util.*;

/**
 * An effect that carries out the capturing of a single character on table.
 */
class CaptureOneCharacterOnTableEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _performingPlayerId;
    private PhysicalCard _characterToCapture;
    private boolean _freezeCharacter;
    private PhysicalCard _cardFiringWeapon;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private CaptureOneCharacterOnTableEffect _that;

    /**
     * Creates an effect that carries out the capturing of a single character on table.
     * @param action the action performing this effect
     * @param characterToCapture the character to capture
     * @param freezeCharacter true if the character is 'frozen' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public CaptureOneCharacterOnTableEffect(Action action, PhysicalCard characterToCapture, boolean freezeCharacter, PhysicalCard cardFiringWeapon) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _characterToCapture = characterToCapture;
        _freezeCharacter = freezeCharacter && !characterToCapture.isFrozen();
        _cardFiringWeapon = cardFiringWeapon;
        _that = this;
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

        // 1) Trigger is "about to be captured" for card to be captured.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified card from being captured.
        subAction.appendEffect(new TriggeringResultEffect(subAction, new AboutToCaptureCardResult(subAction, _characterToCapture, _that)));

        // 2) Check if card is still to be captured
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (isEffectOnCardPrevented(_characterToCapture)) {
                            return;
                        }

                        final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, _characterToCapture);
                        final boolean wasUndercover = _characterToCapture.isUndercover();
                        final boolean wasMissing = _characterToCapture.isMissing();

                        // 3) Find missing character (if needed), but don't emit any effect result for it.
                        if (wasMissing) {
                            gameState.findMissingCharacter(game, _characterToCapture);
                        }

                        // 4) 'Break cover' (if needed), but don't emit any effect result for it.
                        if (wasUndercover) {
                            gameState.breakCover(_characterToCapture);
                        }

                        // 5) Freeze character (if needed), but don't emit any effect result for it.
                        if (_freezeCharacter) {
                            gameState.freezeCharacter(_characterToCapture);
                        }

                        //
                        // 6) Determine which options are available: 'Seizure', 'Imprisonment', 'Escape', or 'Leave Unattended'
                        //
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {

                                        // Check if 'Seizure' is a valid option
                                        final Collection<PhysicalCard> validToSeizeCaptive = Filters.filterActive(game, null,
                                                Filters.and(Filters.owner(_performingPlayerId), Filters.canEscortCaptive(_characterToCapture), Filters.atSameLocation(_characterToCapture)));

                                        // Check if 'Imprisonment' is a valid option
                                        boolean isAtPrison = Filters.prison.accepts(game.getGameState(), game.getModifiersQuerying(), location);

                                        if (!validToSeizeCaptive.isEmpty() || isAtPrison) {
                                            List<String> choices = new ArrayList<String>();
                                            if (!validToSeizeCaptive.isEmpty()) choices.add(CaptureOption.SEIZE.getHumanReadable());
                                            if (isAtPrison) choices.add(CaptureOption.IMPRISONMENT.getHumanReadable());
                                            if (_characterToCapture.isFrozen()) choices.add(CaptureOption.LEAVE_UNATTENDED.getHumanReadable());
                                            if (!_characterToCapture.isFrozen()) choices.add(CaptureOption.ESCAPE.getHumanReadable());
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
                                                                                        subAction.appendEffect(
                                                                                                new CaptureWithSeizureEffect(subAction, _characterToCapture, wasUndercover, wasMissing, escort, _cardFiringWeapon));
                                                                                    }
                                                                                });
                                                                    }
                                                                    else if (result.equals(CaptureOption.IMPRISONMENT.getHumanReadable())) {
                                                                        // Capture with 'Imprisonment'
                                                                        subAction.appendEffect(
                                                                                new CaptureWithImprisonmentEffect(subAction, _characterToCapture, wasUndercover, wasMissing, _cardFiringWeapon));
                                                                    }
                                                                    else if (result.equals(CaptureOption.LEAVE_UNATTENDED.getHumanReadable())) {
                                                                        // Capture with 'Leave Unattended'
                                                                        subAction.appendEffect(
                                                                                new CaptureWithLeaveUnattendedEffect(subAction, _characterToCapture, wasUndercover, wasMissing, _cardFiringWeapon));
                                                                    }
                                                                    else {
                                                                        // Capture with 'Escape'
                                                                        subAction.appendEffect(
                                                                                new CaptureWithEscapeEffect(subAction, _characterToCapture, wasUndercover, wasMissing, _cardFiringWeapon));
                                                                    }
                                                                }
                                                            }));
                                        }
                                        else if (_characterToCapture.isFrozen()) {
                                            // Capture with 'Leave Unattended' is only option
                                            subAction.appendEffect(
                                                    new CaptureWithLeaveUnattendedEffect(subAction, _characterToCapture, wasUndercover, wasMissing, _cardFiringWeapon));
                                        }
                                        else {
                                            // Capture with 'Escape' is only option
                                            subAction.appendEffect(
                                                    new CaptureWithEscapeEffect(subAction, _characterToCapture, wasUndercover, wasMissing, _cardFiringWeapon));
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }
}
