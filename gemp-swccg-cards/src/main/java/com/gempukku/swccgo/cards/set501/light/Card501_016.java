package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;

/**
 * Set: Set 15
 * Type: Weapon
 * Subtype: Character
 * Title: Uncivilized Blaster
 */
public class Card501_016 extends AbstractCharacterWeapon {
    public Card501_016() {
        super(Side.LIGHT, 3, "Uncivilized Blaster");
        setLore("");
        setGameText("Deploy on your warrior. May target a character, creature or vehicle for free. Draw destiny. If destiny + 2 > target’s defense value, target hit. If hit by Corran, Kanan or Obi-Wan, may not be used to satisfy attrition)");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.BLASTER);
        setTestingText("Uncivilized Blaster");
        hideFromDeckBuilder();
    }
}
