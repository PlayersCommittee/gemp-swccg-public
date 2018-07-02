package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ConvertByReplacingCharacterEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * An action to convert a character by replacing that character with another character of the same persona.
 */
public class ConvertByReplacingCharacterAction extends AbstractTopLevelRuleAction {
    private ConvertByReplacingCharacterAction _that;
    private PhysicalCard _newCharacter;
    private PhysicalCard _characterToReplace;
    private boolean _characterConverted;
    private ConvertByReplacingCharacterEffect _convertCharacterEffect;

    /**
     * Creates an action to persona replace a character with another character of the same persona.
     * @param newCharacter the character to replace with
     * @param oldCharacter the character to be replaced
     */
    public ConvertByReplacingCharacterAction(final PhysicalCard newCharacter, PhysicalCard oldCharacter) {
        super(newCharacter, newCharacter.getOwner());
        _newCharacter = newCharacter;
        _text = "Convert " + GameUtils.getFullName(oldCharacter);
        _that = this;

        appendTargeting(
                new TargetCardOnTableEffect(_that, getPerformingPlayer(), "Choose character to convert", SpotOverride.INCLUDE_UNDERCOVER, oldCharacter) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                        _characterToReplace = target;
                    }
                }
        );
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    @Override
    public void setText(String text) {
        _text = text;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            // Perform any costs in the queue
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Convert the character
            if (!_characterConverted) {
                _characterConverted = true;

                _convertCharacterEffect = new ConvertByReplacingCharacterEffect(_that, _characterToReplace, _newCharacter);
                return _convertCharacterEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _characterConverted && _convertCharacterEffect.wasCarriedOut();
    }
}
