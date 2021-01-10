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
class CaptureOneStarshipOnTableEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _performingPlayerId;
    private PhysicalCard _starshipToCapture;
    private PhysicalCard _attachTo;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private CaptureOneStarshipOnTableEffect _that;

    /**
     * Creates an effect that carries out the capturing of a single character on table.
     * @param action the action performing this effect
     * @param starshipToCapture the starship to capture
     * @param attachTo the card to which the starship will be attached
     */
    public CaptureOneStarshipOnTableEffect(Action action, PhysicalCard starshipToCapture, PhysicalCard attachTo) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _starshipToCapture = starshipToCapture;
        _attachTo = attachTo;
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
        subAction.appendEffect(new TriggeringResultEffect(subAction, new AboutToCaptureCardResult(subAction, _starshipToCapture, _that)));

        // 2) Check if card is still to be captured
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (isEffectOnCardPrevented(_starshipToCapture)) {
                            return;
                        }

                        final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, _starshipToCapture);

                        //
                        // 3) Capture the starship
                        //
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Capture with 'Seize' is only option
                                            subAction.appendEffect(
                                                    new CaptureStarshipEffect(subAction, _starshipToCapture, _attachTo));
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
