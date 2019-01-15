package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * Set: Set 10
 * Type: Objective
 * Title: Ralltiir Operations (V) / In The Hands Of The Empire (V)
 */
public class Card210_042 extends AbstractObjective {
    public Card210_042() {
        super(Side.DARK, 0, Title.Ralltiir_Operations);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Ralltiir system. For remainder of game, spaceport sites are immune to He Hasn't Come Back Yet and Ounee Ta. Your Force generation is +1 at each Ralltiir location. Once per battle, when you draw battle destiny, may exchange a card in hand with a card of same card type in Lost Pile. While this side up, once per turn, may deploy from Reserve Deck a site (or non-unique Imperial) to Ralltiir. Flip this card if Imperials control at least three Ralltiir sites and opponent controls no Ralltiir locations.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return deployCardWithObjectiveText(self, Filters.Ralltiir_system, "Ralltiir system");
    }
}
