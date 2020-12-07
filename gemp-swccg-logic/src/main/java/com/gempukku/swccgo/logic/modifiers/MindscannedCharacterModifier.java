package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;

/**
 * A modifier to be used when a character is mindscanned
 */
public class MindscannedCharacterModifier extends AbstractModifier {
    private PhysicalCard _mindscannedCharacter;
    private boolean _gameTextWasCanceled;

    /**
     * Creates a modifier for mindscanning a character
     * @param source the card that did the mindscanning
     * @param mindscannedCharacter the card that was mindscanned
     * @param gameTextWasCanceled if the mindscanned card's game text was canceled when it was mindscanned
     */
    public MindscannedCharacterModifier(PhysicalCard source, PhysicalCard mindscannedCharacter, boolean gameTextWasCanceled) {
        super(source, "Mindscanned "+ GameUtils.getCardLink(mindscannedCharacter), source, ModifierType.MINDSCANNED_CHARACTER);
        _gameTextWasCanceled = gameTextWasCanceled;
        try {
            _mindscannedCharacter = mindscannedCharacter.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public PhysicalCard getmindScannedCharacter() {
        return _mindscannedCharacter;
    }
    public boolean wasGameTextCanceled() {
        return  _gameTextWasCanceled;
    }
}
