package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ModifyManeuverUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 27
 * Type: Interrupt
 * Subtype: Used
 * Title: Watch That Crossfire, Boys
 */
public class Card227_027 extends AbstractUsedInterrupt {
    public Card227_027() {
        super(Side.LIGHT, 6, "Watch That Crossfire, Boys", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Many pilots for the Rebellion learned their skills using modified T-47s and other airspeeders. Being able to weave in and out of combat has become second nature to them.");
        setGameText("Add 2 to power and maneuver of your T-47 until end of turn. (Interrupt may even affect the result of a destiny draw targeting the T-47's maneuver.) OR Once per game, if you are about to lose Force, reduce the loss by 1 for each marker site you occupy.");
        addIcons(Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.T_47);

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
        List<PlayInterruptAction> actions = new ArrayList<PlayInterruptAction>();
        Filter t47Filter = Filters.and(Filters.your(self), Filters.T_47);

        // Affect the result of a destiny draw targeting the T-47's maneuver
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, t47Filter)) {
            Collection<PhysicalCard> targetedCards = ((DestinyDrawnResult) effectResult).getAbilityManeuverOrDefenseValueTargeted();

            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), t47Filter));
            if (action != null) {
                actions.add(action);
            }
        }

        // Once per game, if you are about to lose Force, reduce the loss by 1 for each marker site you occupy
        GameTextActionId gameTextActionId = GameTextActionId.WATCH_THAT_CROSSFIRE_BOYS__REDUCE_FORCE_LOSS;
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
            && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (!result.isCannotBeReduced(game)) {
                int numMarkerSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.marker_site, Filters.occupies(playerId)));
                if (numMarkerSites > 0) {
                    final int reduceAmount = numMarkerSites;

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Reduce Force loss");
                    action.appendUsage(
                        new OncePerGameEffect(action));
                    // Allow response(s)
                    action.allowResponses("Reduce Force loss by " + reduceAmount,
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                    new ReduceForceLossEffect(action, playerId, reduceAmount));
                            }
                        }
                    );
                    actions.add(action);
                }
            }
        }

        return actions;
    }

    private PlayInterruptAction generatePlayInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add 2 to power and maneuver");
        // Choose target(s)
        action.appendTargeting(
            new TargetCardOnTableEffect(action, playerId, "Choose T-47", filter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.addAnimationGroup(targetedCard);
                    // Allow response(s)
                    action.allowResponses("Add 2 to power and maneuver of " + GameUtils.getCardLink(targetedCard),
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
                                    new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, 2));
                            }
                        }
                    );
                }
            }
        );
        return action;
    }
}
