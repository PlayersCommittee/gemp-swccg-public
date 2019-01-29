package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;

/**
 * Set: Set 10
 * Type: Objective
 * Title: The Hyperdrive Generator's Gone (V) / We'll Need A New One (V)
 */
public class Card210_025 extends AbstractObjective {
    public Card210_025() {
        super(Side.LIGHT, 0, Title.The_Hyperdrive_Generators_Gone);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy City Outskirts, Watto's Junkyard, and Credits Will Do Fine. For remainder of game, you may not deploy cards with ability except unique (•) aliens, Republic characters, [Republic] starships, and [Episode I] Jedi. Your Destiny is suspended. You lose no Force from [Reflections II] objectives. While this side up, once per game, may take into hand from Reserve Deck an [Episode I] system. Unless present with Qui-Gon, Maul is immune to attrition. Flip this card if there are four or more cards beneath Credits Will Do Fine.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.CORUSCANT, Icon.EPISODE_I);
    }

}
