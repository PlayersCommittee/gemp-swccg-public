package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Spaceport Speeders
 */
public class Card1_112 extends AbstractUsedInterrupt {
    public Card1_112() {
        super(Side.LIGHT, 6, "Spaceport Speeders", Uniqueness.RESTRICTED_3);
        setLore("Spaceport Speeders buys, trades and sells floaters. Wioslea is known as a shrewd bargainer. Luke got 2,000 credits for his X-24 speeder.");
        setGameText("Sell one of your vehicles or droids at Mos Eisley or same site as Wioslea. Draw two destiny (three destiny if vehicle is Luke's X-34 Landspeeder). The total is the 'offer,' which you must accept. Activate that much Force; then vehicle or droid is lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.droid, Filters.vehicle), Filters.at(Filters.or(Filters.Mos_Eisley, Filters.sameSiteAs(self, Filters.Wioslea),Filters.sameSiteAs(self, Filters.Droid_Merchant))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Sell vehicle or droid");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle or droid", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Sell " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            int numDestinyDraws = Filters.Lukes_X34_Landspeeder.accepts(game, finalTarget) ? 3 : 2;
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, numDestinyDraws) {
                                                        @Override
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            float offer = (totalDestiny != null) ? totalDestiny : 0;
                                                            game.getGameState().sendMessage(playerId + " sells " + GameUtils.getCardLink(finalTarget) + " for " + GuiUtils.formatAsString(offer));
                                                            action.appendEffect(
                                                                    new ActivateForceEffect(action, playerId, (int) Math.floor(offer)));
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, finalTarget));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}