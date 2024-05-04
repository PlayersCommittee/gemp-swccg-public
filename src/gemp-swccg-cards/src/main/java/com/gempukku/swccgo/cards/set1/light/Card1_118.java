package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Utinni!
 */
public class Card1_118 extends AbstractUsedInterrupt {
    public Card1_118() {
        super(Side.LIGHT, 4, Title.Utinni, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Jawa trade language word for 'Come here!' Jawas work communally and scavenge for equipment.");
        setGameText("If one of your Jawas is at the same location as any Utinni Effect, 'steal' that Utinni Effect into your hand, to use or lose as your own. OR Cancel any Interrupt or Effect with 'Jawa' in the card name.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter filter1 = Filters.and(Filters.Utinni_Effect, Filters.at(Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Jawa, Filters.canBeTargetedBy(self)))));
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("'Steal' Utinni Effect");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Utinni Effect", targetingReason, filter1) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("'Steal' " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new StealCardIntoHandFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter2 = Filters.and(Filters.Effect, Filters.titleContains("Jawa"));

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter2, "Effect with 'Jawa' in name");
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.and(Filters.or(Filters.Interrupt, Filters.Effect), Filters.titleContains("Jawa"));

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
