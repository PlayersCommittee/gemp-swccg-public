package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;

public class Card211_026 extends AbstractObjective {
    public Card211_026() {
        super(Side.DARK, 0, Title.A_Stunning_Move);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy 500 Republica (with Insidious Prisoner there) and Private Platform.\n" +
                "For remainder of game, you may not deploy Sidious, First Order characters, or Imperials. Grievous is immunity to attrition +2. Once per turn, may \\/ an Invisible Hand site or a non-unique [Separatist] droid. \n" +
                "Flip this card if Insidious Prisoner is at an Invisible Hand site.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction actions = new ObjectiveDeployedTriggerAction(self);
        actions.appendRequiredEffect(getDeployCardFromReserveDeckEffect(actions, Filters._500_Republica, "Choose 500 Republica to deploy"));
        actions.appendRequiredEffect(getDeployCardToTargetFromReserveDeckEffect(actions, Filters.Insidious_Prisoner, Filters._500_Republica, "Choose Insidious Prisoner to deploy to 500 Republica"));
        actions.appendRequiredEffect(getDeployCardFromReserveDeckEffect(actions, Filters.Private_Platform, "Choose Private Platform to deploy"));
        return actions;
    }
}
