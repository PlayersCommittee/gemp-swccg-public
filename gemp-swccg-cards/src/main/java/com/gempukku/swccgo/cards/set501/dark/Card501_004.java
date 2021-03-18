package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used
 * Title: Shocking Revelation (V)
 */
public class Card501_004 extends AbstractUsedInterrupt {
    public Card501_004() {
        super(Side.DARK, 5, Title.Shocking_Revelation, Uniqueness.UNIQUE);
        setLore("'Well, don't blame me. I'm an interpreter. I'm not supposed to know a power socket from a computer terminal.'");
        setGameText("Each player reveals the top 2 cards from their reserve deck. For every destiny = 5 revealed, choose one action: peek at opponent’s hand OR take one of your revealed cards into hand OR activate 1 Force. Return revealed cards to owners’ Reserve deck; reshuffle.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Shocking Revelation (V)");
    }
}
