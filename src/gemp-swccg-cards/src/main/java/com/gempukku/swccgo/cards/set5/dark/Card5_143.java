package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Heart Of The Chasm
 */
public class Card5_143 extends AbstractLostInterrupt {
    public Card5_143() {
        super(Side.DARK, 3, Title.Heart_Of_The_Chasm, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("The Cloud City chasm's central fin was engineered with a tensile strength unsurpassed by anything in the region, save the silk of the Kashyyyk treeworm.");
        setGameText("During your deploy phase, use X Force to rearrange all Cloud City sites, where X = total number of those sites. All cards at a given site move along with that site. OR Cancel Off The Edge. OR Search your Reserve Deck, take one Weather Vane into hand and reshuffle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Rearrange uses parameterized interior Bespin/Cloud City sites (product direction: not Death-Star-hardcoded).
        // Printed "Cloud City sites" maps to Bespin interior-only group via RearrangeSites (docking bays excluded).
        final Filter interiorCloudCitySites = RearrangeSites.interiorSitesOfSystem(Title.Bespin);
        final int x = Filters.countTopLocationsOnTable(game, interiorCloudCitySites);

        // Check condition(s) - rearrange interior Cloud City sites
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && x > 0
                && RearrangeSites.canRearrangeInteriorSites(game, Title.Bespin)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, x)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Rearrange interior Cloud City sites");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, x));
            // Allow response(s)
            action.allowResponses("Rearrange all interior Cloud City sites",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseAndRearrangeRelatedSitesEffect(action, playerId, interiorCloudCitySites));
                        }
                    }
            );
            actions.add(action);
        }

        Filter offTheEdge = Filters.Off_The_Edge;

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, offTheEdge)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, offTheEdge, Title.Off_The_Edge);
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.HEART_OF_THE_CHASM__UPLOAD_WEATHER_VANE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Weather Vane into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Weather_Vane, true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter offTheEdge = Filters.Off_The_Edge;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, offTheEdge)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
