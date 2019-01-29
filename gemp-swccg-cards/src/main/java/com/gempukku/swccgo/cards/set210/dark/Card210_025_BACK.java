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
public class Card210_025_BACK extends AbstractObjective {
    public Card210_025_BACK() {
        super(Side.LIGHT, 7, Title.Well_Need_A_New_One);
        setVirtualSuffix(true);
        setGameText("While this side up, your unique (•) Republic characters are power +1 and forfeit +2. Aliens may not have their deploy cost modified to Tatooine locations. Whenever you complete a non-substituted battle destiny draw, may retrieve 1 Force (Force retrieved in this way may be taken into hand). Once during opponent's turn, if Queen's Royal Starship at a system, may activate up to 2 Force. During your control phase, opponent loses 1 Force for each battleground occupied by Amidala or Jar Jar.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.CORUSCANT, Icon.EPISODE_I);
    }
}
