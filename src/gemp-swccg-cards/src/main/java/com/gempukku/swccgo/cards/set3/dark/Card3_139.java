package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Turn It Off! Turn It Off!
 */
public class Card3_139 extends AbstractUsedOrLostInterrupt {
    public Card3_139() {
        super(Side.DARK, 5, Title.Turn_It_Off_Turn_It_Off, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C1);
        setLore("'Turn it off! Turn it off! Off! TURN IT OFF!'");
        setGameText("USED: Cancel any attempt to place a 'hit' starship, vehicle or droid in the Used Pile rather than the Lost Pile. OR Cancel Han's Toolkit. LOST: Cancel Crash Site Memorial.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s) - USED: Cancel attempt to place hit starship/vehicle/droid in Used instead of Lost
        Filter hitStarshipVehicleOrDroid = Filters.and(Filters.or(Filters.starship, Filters.vehicle, Filters.droid), Filters.hit, Filters.canBeTargetedBy(self));
        List<TargetingReason> targetingReasons = Collections.singletonList(TargetingReason.TO_BE_USED_INSTEAD_OF_LOST);
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, hitStarshipVehicleOrDroid, targetingReasons)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, hitStarshipVehicleOrDroid);
            if (!cardsTargeted.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Cancel targeting");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose hit starship, vehicle, or droid", Filters.in(cardsTargeted)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Cancel attempt to place " + GameUtils.getCardLink(cardTargeted) + " in Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelTargetingEffect(action, respondableEffect));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        // Check condition(s) - USED: Cancel Han's Toolkit being played
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Hans_Toolkit)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        // Check condition(s) - LOST: Cancel Crash Site Memorial being played
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Crash_Site_Memorial)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) - USED: Cancel Han's Toolkit on table
        if (GameConditions.canTargetToCancel(game, self, Filters.Hans_Toolkit)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Hans_Toolkit, Title.Hans_Toolkit);
            actions.add(action);
        }
        // Check condition(s) - LOST: Cancel Crash Site Memorial on table
        if (GameConditions.canTargetToCancel(game, self, Filters.Crash_Site_Memorial)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Crash_Site_Memorial, Title.Crash_Site_Memorial);
            actions.add(action);
        }
        return actions;
    }
}
