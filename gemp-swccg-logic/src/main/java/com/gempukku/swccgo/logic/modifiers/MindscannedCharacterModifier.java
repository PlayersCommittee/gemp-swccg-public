package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;

/**
 * A modifier to be used when a character is mindscanned
 */
public class MindscannedCharacterModifier extends AbstractModifier {
    private PhysicalCard _mindscannedCharacter;

    /**
     * Creates a modifier for mindscanning a character
     * @param source the card that did the mindscanning
     * @param mindscannedCharacter the card that was mindscanned
     */
    public MindscannedCharacterModifier(PhysicalCard source, PhysicalCard mindscannedCharacter) {
        super(source, "Mindscanned "+ GameUtils.getCardLink(mindscannedCharacter), source, ModifierType.MINDSCANNED_CHARACTER);
        try {
            _mindscannedCharacter = mindscannedCharacter.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public PhysicalCard getmindScannedCharacter() {
        return _mindscannedCharacter;
    }
}
