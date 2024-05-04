package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Trash Compactor (V)
 */
public class Card215_009 extends AbstractSite {
    public Card215_009() {
        super(Side.LIGHT, Title.Trash_Compactor, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Unless you control an adjacent site, We're All Gonna Be A Lot Thinner! is canceled.");
        setLocationLightSideGameText("During your move phase, you may move to here for free from any Death Star site.");
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, playerOnDarkSideOfLocation, Filters.Were_All_Gonna_Be_A_Lot_Thinner)
                && !GameConditions.controls(game, playerOnDarkSideOfLocation, Filters.adjacentSite(self))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Were_All_Gonna_Be_A_Lot_Thinner, Title.Were_All_Gonna_Be_A_Lot_Thinner);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter otherDeathStarSite = Filters.and(Filters.other(self), Filters.Death_Star_site);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherDeathStarSite)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, otherDeathStarSite, self, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.any, otherDeathStarSite, self, true);
                action.setText("Move from other Death Star site to here");
                actions.add(action);
            }
        }
        return actions;
    }
}
