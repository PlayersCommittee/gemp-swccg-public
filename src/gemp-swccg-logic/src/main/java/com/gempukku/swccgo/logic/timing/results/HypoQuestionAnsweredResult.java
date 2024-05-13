package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a player answers a Hypo question.
 */
public class HypoQuestionAnsweredResult extends EffectResult {
    private Answer _answer;

    public enum Answer {
        NO("no"),
        YES("yes");

        private String _humanReadable;

        Answer(String humanReadable) {
            _humanReadable = humanReadable;
        }

        public String getHumanReadable() {
            return _humanReadable;    
        }
    }

    /**
     * Creates an effect result that is emitted when a player answers a Hypo question.
     * @param playerId the player that answered the Hypo question
     * @param answer the answer
     */
    public HypoQuestionAnsweredResult(String playerId, Answer answer) {
        super(Type.HYPO_QUESTION_ANSWERED, playerId);
        _answer = answer;
    }

    /**
     * Gets the answer.
     * @return the answered
     */
    public Answer getAnswer() {
        return _answer;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Answered '" + _answer.getHumanReadable() + "' to Hypo question";
    }
}
