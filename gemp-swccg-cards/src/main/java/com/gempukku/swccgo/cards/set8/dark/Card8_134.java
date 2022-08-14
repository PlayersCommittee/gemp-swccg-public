package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ModifyManeuverUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Accelerate
 */
public class Card8_134 extends AbstractUsedInterrupt {
    public Card8_134() {
        super(Side.DARK, 6, "Accelerate");
        setLore("The Empire trains its personnel to operate a variety of specialized equipment in demanding environments. This training allows troops to take seemingly risky actions.");
        setGameText("Adds 2 to maneuver, 1 to power and 1 to landspeed of either your speeder bike piloted by a biker scout or your swoop for remainder of turn. (Interrupt may even affect the result just after a destiny draw targeting the vehicle's maneuver.)");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.speeder_bike, Filters.hasPiloting(self, Filters.biker_scout)), Filters.and(Filters.swoop, Filters.driven)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, filter);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.speeder_bike, Filters.hasPiloting(self, Filters.biker_scout)), Filters.and(Filters.swoop, Filters.piloted)));

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, filter)) {
            Collection<PhysicalCard> targetedCards = ((DestinyDrawnResult) effectResult).getAbilityManeuverOrDefenseValueTargeted();

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), filter));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction generatePlayInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add 2 to maneuver, 1 to power, and 1 to landspeed");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose speeder bike or swoop", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Add 2 to maneuver, 1 to power, and 1 to landspeed of " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyManeuverUntilEndOfTurnEffect(action, finalTarget, 2));
                                        action.appendEffect(
                                                new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, 1));
                                        action.appendEffect(
                                                new ModifyLandspeedUntilEndOfTurnEffect(action, finalTarget, 1));
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}