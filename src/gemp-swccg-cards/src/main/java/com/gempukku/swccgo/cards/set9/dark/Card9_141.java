package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeBattledModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Lost
 * Title: Young Fool
 */
public class Card9_141 extends AbstractLostInterrupt {
    public Card9_141() {
        super(Side.DARK, 6, "Young Fool", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("'Now, young Skywalker ... you will die.'");
        setGameText("If opponent's character present with Emperor was just lost, lose 1 Force to place that character out of play. OR Release frozen Luke at your Throne Room (Luke may not be battled until end of your next turn) OR Cancel NOOOOOOOOOOOO!");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justLostWasPresentWith(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.Emperor)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " out of play");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(justLostCard) + " out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromOffTableEffect(action, justLostCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter lukeFilter = Filters.and(Filters.Luke, Filters.frozenCaptive, Filters.at(Filters.and(Filters.your(self), Filters.Throne_Room)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, lukeFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Release frozen Luke");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose frozen Luke", lukeFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Release " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReleaseCaptiveEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new AddUntilEndOfPlayersNextTurnModifierEffect(action,
                                                            playerId, new MayNotBeBattledModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " not able to be battled"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.NOOOOOOOOOOOO)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.NOOOOOOOOOOOO, Title.NOOOOOOOOOOOO);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.NOOOOOOOOOOOO)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}