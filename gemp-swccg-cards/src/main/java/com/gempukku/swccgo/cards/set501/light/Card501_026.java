package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;

/**
 * Set: Set 15
 * Type: Weapon
 * Subtype: Character
 * Title: Stolen Stormtrooper Blaster Rifle
 */
public class Card501_026 extends AbstractCharacterWeapon {
    public Card501_026() {
        super(Side.LIGHT, 3, "Stolen Stormtrooper Blaster Rifle");
        setLore("");
        setGameText("Deploy on your warrior (warrior is power +1 if a stormtrooper here). May target a character, creature, or vehicle for free. Draw destiny; add 1 if targeting a character, 2 if a vehicle. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.");
        addIcons(Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.BLASTER_RIFLE);
        setTestingText("Stolen Stormtrooper Blaster Rifle");
        hideFromDeckBuilder();
    }
}
