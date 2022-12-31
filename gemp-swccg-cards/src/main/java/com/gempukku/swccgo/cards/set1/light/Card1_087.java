package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceHandInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: How Did We Get Into This Mess?
 */
public class Card1_087 extends AbstractUsedInterrupt {
    public Card1_087() {
        super(Side.LIGHT, 4, Title.How_Did_We_Get_Into_This_Mess, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Stranded in the Dune Sea, R2-D2 insisted on heading into rocky canyons where he thought settlements were likely to exist. Threepio had other ideas.");
        setGameText("If one of your droids is at the Dune Sea or where there is a Scomp link, use 1 Force to discard your entire hand to your Used Pile. Draw the same number of cards from your Reserve Deck. Draw one extra card if droid is C-3PO or R2-D2.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.droid, Filters.or(Filters.at(Filters.Dune_Sea), Filters.at_Scomp_Link));

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place hand in Used Pile and draw cards");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a droid", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedDroid) {
                            action.addAnimationGroup(targetedDroid);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.sameLocationAs(self, Filters.inActionTargetGroup(action, targetGroupId1)));
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Place hand in Used Pile and draw cards from Reserve Deck by targeting " + GameUtils.getCardLink(targetedDroid),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalDroid = action.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            int numCardsToDraw = game.getGameState().getHand(playerId).size();
                                            if (Filters.or(Filters.C3PO, Filters.R2D2).accepts(game, finalDroid)) {
                                                numCardsToDraw++;
                                            }
                                            action.appendEffect(
                                                    new PlaceHandInUsedPileEffect(action, playerId));
                                            action.appendEffect(
                                                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numCardsToDraw));
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