package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Exhaustion
 */
public class Card3_123 extends AbstractLostInterrupt {
    public Card3_123() {
        super(Side.DARK, 5, "Exhaustion", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLore("'Sir, the temperature's dropping too rapidly.' 'That's right, and my friend's out in it.'");
        setGameText("Opponent must lose 1 Force for each of opponent's missing characters on table. OR Select one missing character under 'nighttime conditions' to be lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.opponents(self), Filters.missing, Filters.character);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_MISSING, filter)) {
            final int count = Filters.countActive(game, self, SpotOverride.INCLUDE_MISSING, filter);

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose " + count + "Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, count));
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter2 = Filters.and(Filters.missing, Filters.character, Filters.under_nighttime_conditions);
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_MISSING, targetingReason, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make missing character lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose missing character", SpotOverride.INCLUDE_MISSING, targetingReason, filter2) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}