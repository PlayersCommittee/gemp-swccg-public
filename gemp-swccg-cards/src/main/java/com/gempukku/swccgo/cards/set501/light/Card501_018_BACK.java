package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 15
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself (V)
 */
public class Card501_018_BACK extends AbstractObjective {
    public Card501_018_BACK() {
        super(Side.LIGHT, 7, Title.Sometimes_I_Amaze_Even_Myself);
        setGameText("For remainder of game I Can't Believe He's Gone may only add power in battles involving Luke or Leia. You retrieve no Force from Detention Bloc Corridor." +
                "While this side up, whenever you 'hit' a character with a blaster, opponent loses 1 Force. May place Obi-Wan out of play from a Death Star site to cancel a battle at another Death Star site If Leia is about to be removed from table, either player may imprison her in Detention Block Corridor instead." +
                "Flip this card if Leia is not on table.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Rescue The Princess / Sometimes I Amaze Even Myself (V)");
        hideFromDeckBuilder();
    }
}
