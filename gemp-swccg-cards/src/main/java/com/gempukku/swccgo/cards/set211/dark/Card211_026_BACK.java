package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

public class Card211_026_BACK extends AbstractObjective {
    public Card211_026_BACK() {
        super(Side.DARK, 7, "A Valuable Hostage");
        setGameText("While this side up, immunity to attrition of opponent's Jedi, starships, and vehicles is limited to < 5. During your control phase, if your [Separatist] character with Insidious Prisoner, opponent loses 1 Force. Once during your turn, may search your Force Pile and reveal any one card; reshuffle. Opponent may lose 2 Force to place that card on bottom of your Used Pile; otherwise, take it into hand.\n" +
                "Flip this card if Insidious Prisoner is not at an Invisible Hand site.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    // TODO Immunity limited to < 5

    // TODO Opponent loses 1 force

    // TODO Force pile reveal

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard insidiousPrisoner = Filters.findFirstActive(game, self, Filters.Insidious_Prisoner);
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (!GameConditions.isAttachedTo(game, insidiousPrisoner, Filters.Invisible_Hand_site))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
