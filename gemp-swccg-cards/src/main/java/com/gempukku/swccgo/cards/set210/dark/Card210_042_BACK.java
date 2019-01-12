package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 10
 * Type: Objective
 * Title: Ralltiir Operations (V) / In The Hands Of The Empire (V)
 */
public class Card210_042_BACK extends AbstractObjective {
    public Card210_042_BACK() {
        super(Side.DARK, 7, Title.In_The_Hands_Of_The_Empire);
        setVirtualSuffix(true);
        setGameText("Immediately, may take into hand from Reserve Deck any one card. While this side up, opponent's Force drains are -1 at non-Ralltiir locations. Your total battle destiny is +X, where X = number of Ralltiir locations your Imperials occupy. Always Thinking With Your Stomach is canceled. Flip this card and place a card from hand on Used Pile (if possible) if opponent controls at least two Ralltiir locations.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
    }
}
