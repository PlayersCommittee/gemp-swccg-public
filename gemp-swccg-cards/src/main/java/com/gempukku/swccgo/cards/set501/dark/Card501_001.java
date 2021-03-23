package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Alien
 * Title: Alien Mob
 */
public class Card501_001 extends AbstractAlien {
    public Card501_001() {
        super(Side.DARK, 3, null, 6, 3, 6, "Alien Mob", Uniqueness.DIAMOND_2);
        setLore("");
        setGameText("* Replaces any 3 of your aliens at same Jabba’s Palace site (aliens go to Used Pile) or deploys for 5 Force. When deployed, may retrieve your Rep OR place your Rep stacked on your objective in Used pile. This alien assumes your Rep's species (if any).");
        addIcons(Icon.WARRIOR, Icon.WARRIOR, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        setTestingText("Alien Mob");
        hideFromDeckBuilder();
    }
}
