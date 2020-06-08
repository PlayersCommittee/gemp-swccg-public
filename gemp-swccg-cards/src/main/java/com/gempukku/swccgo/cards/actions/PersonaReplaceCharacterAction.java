package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.PersonaReplaceCharacterEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * An action to persona replace a character with another character of the same persona.
 */
public class PersonaReplaceCharacterAction extends AbstractTopLevelRuleAction {
    private PersonaReplaceCharacterAction _that;
    private PhysicalCard _newCharacter;
    private PhysicalCard _characterToReplace;
    private boolean _characterReplaced;
    private PersonaReplaceCharacterEffect _personaReplacementEffect;

    /**
     * Creates an action to persona replace a character with another character of the same persona.
     * @param newCharacter the character to replace with
     * @param oldCharacter the character to be replaced
     */
    public PersonaReplaceCharacterAction(final PhysicalCard newCharacter, PhysicalCard oldCharacter) {
        super(newCharacter, newCharacter.getOwner());
        _newCharacter = newCharacter;
        _text = "Persona replace " + GameUtils.getFullName(oldCharacter);
        _that = this;

        appendTargeting(
                new TargetCardOnTableEffect(_that, getPerformingPlayer(), "Choose character to persona replace", SpotOverride.INCLUDE_UNDERCOVER, oldCharacter) {
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

            // Persona replace the character
            if (!_characterReplaced) {
                _characterReplaced = true;

                _personaReplacementEffect = new PersonaReplaceCharacterEffect(_that, _characterToReplace, _newCharacter);
                return _personaReplacementEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _characterReplaced && _personaReplacementEffect.wasCarriedOut();
    }
}
