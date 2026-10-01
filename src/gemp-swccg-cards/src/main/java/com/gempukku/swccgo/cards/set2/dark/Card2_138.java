package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ChooseAndRearrangeRelatedSitesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Retract The Bridge
 */
public class Card2_138 extends AbstractLostInterrupt {
    public Card2_138() {
        super(Side.DARK, 3, Title.Retract_The_Bridge, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("Strategically retracted bridges can frustrate enemy movement, forcing routing plans to be rearranged.");
        setGameText("During your deploy phase, use X Force to rearrange all interior Death Star sites, where X = total number of those sites. All cards at a given site move along with that site. OR Cancel On The Edge.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter interiorDeathStarSites = RearrangeSites.interiorSitesOfSystem(Title.Death_Star);
        final int x = Filters.countTopLocationsOnTable(game, interiorDeathStarSites);

        // Check condition(s) - rearrange interior Death Star sites (DS1 only; Docking Bay 327 excluded by helper)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && x > 0
                && RearrangeSites.canRearrangeInteriorSites(game, Title.Death_Star)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, x)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Rearrange interior Death Star sites");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, x));
            // Allow response(s)
            action.allowResponses("Rearrange all interior Death Star sites",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseAndRearrangeRelatedSitesEffect(action, playerId, interiorDeathStarSites));
                        }
                    }
            );
            actions.add(action);
        }

        Filter onTheEdge = Filters.title("On The Edge");

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, onTheEdge)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, onTheEdge, "On The Edge");
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter onTheEdge = Filters.title("On The Edge");

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, onTheEdge)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}