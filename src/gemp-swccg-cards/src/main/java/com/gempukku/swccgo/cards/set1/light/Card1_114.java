package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Thank The Maker
 */
public class Card1_114 extends AbstractLostInterrupt {
    public Card1_114() {
        super(Side.LIGHT, 3, Title.Thank_The_Maker, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Reference to 'One Who Creates,' used by droids in an almost religious way. C-3PO used phrase upon getting a much-needed cleansing oil bath.");
        setGameText("Use 1 Force during opponent's control phase if you have a droid on the table and were drained of at least 5 Force. Draw destiny. That number of cards are randomly selected and retrieved from your Lost Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.CONTROL)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.wasDrainedThisTurnAtLeastForce(game, 5)) {
            Filter droidFilter = Filters.and(Filters.your(self), Filters.droid);
            if (GameConditions.canTarget(game, self, droidFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw destiny to retrieve Force");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose droid", droidFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedDroid) {
                                action.addAnimationGroup(targetedDroid);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Draw destiny to retrieve Force by targeting " + GameUtils.getCardLink(targetedDroid),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                final PhysicalCard finalDroid = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                if (totalDestiny != null && totalDestiny > 0) {
                                                                    if (Filters.mayContributeToForceRetrieval.accepts(game, finalDroid)) {
                                                                        action.appendEffect(
                                                                                new RetrieveForceEffect(action, playerId, totalDestiny, true));
                                                                    } else {
                                                                        action.appendEffect(
                                                                                new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}