package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyHyperspeedUntilEndOfTurnEffect;
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
 * Title: A Few Maneuvers
 */
public class Card1_070 extends AbstractUsedInterrupt {
    public Card1_070() {
        super(Side.LIGHT, 6, "A Few Maneuvers", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("'...I know a few maneuvers. We'll lose them.' Boosted shields and fancy flying are necessary to escape Imperial weapon fire until hyperspace jump can be made.");
        setGameText("Add 2 to hyperspeed and maneuver of any starfighter for the remainder of this turn. (Interrupt may even affect the result immediately after a destiny draw targeting the starfighter's maneuver.)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.starfighter, Filters.hasManeuver, Filters.hasHyperdrive, Filters.piloted);

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
        Filter filter = Filters.and(Filters.starfighter, Filters.hasManeuver, Filters.hasHyperdrive, Filters.piloted);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, Filters.and(filter, Filters.canBeTargetedBy(self)))) {
            Collection<PhysicalCard> targetedCards = ((DestinyDrawnResult) effectResult).getAbilityManeuverOrDefenseValueTargeted();

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), filter));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction generatePlayInterruptAction(final String playerId, SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add 2 to hyperspeed and maneuver");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose starfighter", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Add 2 to hyperspeed and maneuver of " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the final targeted card(s)
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyHyperspeedUntilEndOfTurnEffect(action, finalTarget, 2));
                                        action.appendEffect(
                                                new ModifyManeuverUntilEndOfTurnEffect(action, finalTarget, 2));
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}