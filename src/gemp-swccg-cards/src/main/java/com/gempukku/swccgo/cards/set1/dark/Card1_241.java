package com.gempukku.swccgo.cards.set1.dark;

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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyHyperspeedUntilEndOfTurnEffect;
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
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Dark Maneuvers
 */
public class Card1_241 extends AbstractUsedInterrupt {
    public Card1_241() {
        super(Side.DARK, 6, Title.Dark_Maneuvers, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Imperial TIE fighters, while easily damaged, are very fast and agile. Sophisticated tactics are executed to take advantage of Rebel X-wing and Y-wing weaknesses.");
        setGameText("Add 2 to maneuver and 1 to power of any starfighter for the remainder of this turn. If it has hyperdrive, also add 2 to hyperspeed. (Interrupt may even affect the result immediately after a destiny draw targeting the starfighter's maneuver.)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.starfighter, Filters.hasManeuver, Filters.piloted);

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
        Filter filter = Filters.and(Filters.starfighter, Filters.hasManeuver, Filters.piloted);

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
        action.setText("Add 2 to maneuver and 1 to power");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose starfighter", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Add 2 to maneuver and 1 to power of " + GameUtils.getCardLink(targetedCard),
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
                                        if (Filters.hasHyperdrive.accepts(game, finalTarget)) {
                                            action.appendEffect(
                                                    new ModifyHyperspeedUntilEndOfTurnEffect(action, finalTarget, 2));
                                        }
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}