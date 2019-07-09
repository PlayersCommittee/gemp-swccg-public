package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;

public class Card211_026 extends AbstractObjective {
    public Card211_026() {
        super(Side.DARK, 0, "A Stunning Move");
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy 500 Republica (with Insidious Prisoner there) and Private Platform.\n" +
                "For remainder of game, you may not deploy Sidious, First Order characters, or Imperials. Grievous is immunity to attrition +2. Once per turn, may \\/ an Invisible Hand site or a non-unique [Separatist] droid. \n" +
                "Flip this card if Insidious Prisoner is at an Invisible Hand site.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }
}
