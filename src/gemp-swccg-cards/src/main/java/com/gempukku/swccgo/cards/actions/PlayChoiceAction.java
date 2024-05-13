package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.List;

/**
 * The action to choose which way to play a card from multiple play options.
 */
public class PlayChoiceAction extends AbstractPlayCardAction {
    private List<PlayCardAction> _playCardActionChoices;
    private AbstractPlayCardAction _that;

    /**
     * Creates an action for the specified player to choose which way to play a card from multiple play options.
     * @param playerId the player
     * @param sourceCard the card to initiate the deployment
     * @param cardToPlay the card
     * @param playCardActionChoices the play action choices
     */
    public PlayChoiceAction(String playerId, PhysicalCard sourceCard, PhysicalCard cardToPlay, final List<PlayCardAction> playCardActionChoices) {
        super(cardToPlay, sourceCard);
        _playCardActionChoices = playCardActionChoices;
        _text = "Choose play option for " + GameUtils.getFullName(cardToPlay);
        _that = this;

        String[] actionChoiceTexts = new String[_playCardActionChoices.size()];
        for (int i=0; i<actionChoiceTexts.length; ++i) {
            actionChoiceTexts[i] = _playCardActionChoices.get(i).getText();
        }
        appendEffect(
                new PlayoutDecisionEffect(_that, playerId,
                        new MultipleChoiceAwaitingDecision("Choose play option for " + GameUtils.getCardLink(cardToPlay), actionChoiceTexts) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                PlayCardAction actionChosen = _playCardActionChoices.get(index);
                                actionChosen.setReshuffle(_reshuffle);
                                actionChosen.setPlaceOutOfPlay(_placeOutOfPlay);
                                appendEffect(
                                        new StackActionEffect(_that, actionChosen));
                            }
                        }));
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }
        return null;
    }
}
