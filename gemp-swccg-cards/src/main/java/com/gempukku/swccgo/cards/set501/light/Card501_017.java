package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;

/**
 * Set: Set 15
 * Type: Weapon
 * Subtype: Character
 * Title: Leia's Lightsaber
 */
public class Card501_017 extends AbstractCharacterWeapon {
    public Card501_017() {
        super(Side.LIGHT, 1, "Leia's Lightsaber", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Leia (even with There Is Good In Him instead of Luke's Lightsaber) or Rey. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value.");
        addPersona(Persona.LEIAS_LIGHTSABER);
        addIcons(Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.or(Filters.Leia, Filters.Rey));
        setTestingText("Leia's Lightsaber");
        excludeFromDeckBuilder();
    }
}
