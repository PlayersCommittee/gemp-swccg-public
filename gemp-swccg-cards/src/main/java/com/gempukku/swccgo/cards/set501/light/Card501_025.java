package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.*;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio) (V)
 */
public class Card501_025 extends AbstractDroid {
    public Card501_025() {
        super(Side.LIGHT, 3, 2, 1, 4, "C-3PO (See-Threepio)", Uniqueness.UNIQUE);
        setLore("Cybot Galactica 3PO human-cyborg relations droid. Fluent in over six million forms of communication. 112 years old. Has never been memory-wiped... as far as he knows.");
        setGameText("At battleground sites where you have a droid and a Rebel, opponent may not cancel or modify your Force drains. If on Death Star, cards from A Power Loss go to owner’s Lost Pile instead of Used Pile. Once per game may draw top card of Used Pile.");
        addPersona(Persona.C3PO);
        addModelType(ModelType.PROTOCOL);
        addIcon(Icon.VIRTUAL_SET_15);
        setTestingText("C-3PO (See-Threepio) (V)");
    }
}
