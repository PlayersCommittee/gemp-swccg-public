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
public class Card501_018 extends AbstractObjective {
    public Card501_018() {
        super(Side.LIGHT, 0, Title.Rescue_The_Princess);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Central Core, Trash Compactor, and Detention Block Corridor (with Prisoner 2187 imprisoned there)." +
                "For remainder of game, your Death Star sites generate +1 Force for you and ignore Set Your Course For Alderaan. You may not deploy Jedi (except Obi-Wan)." +
                "While this side up, once per turn you may deploy a Death Star site or A Power Loss from Reserve Deck; reshuffle." +
                "Flip this card if Leia is present at a Death Star site and A Power Loss is 'shut down.'");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Rescue The Princess / Sometimes I Amaze Even Myself (V)");
    }
}
