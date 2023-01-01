package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.BattleDamageLimitModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Freeze!
 */
public class Card8_143 extends AbstractUsedInterrupt {
    public Card8_143() {
        super(Side.DARK, 6, "Freeze!", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Despite Wicket's warnings, an Imperial scout got the drop on Leia.");
        setGameText("If a Rebel just deployed or moved to a site where you have a trooper present armed with a weapon, use 1 Force. For remainder of turn, that Rebel may not battle or move and battle damage against you is limited to 4 at that location.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter rebelFilter = Filters.and(Filters.Rebel, Filters.canBeTargetedBy(self));
        Filter locationFilter = Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.trooper, Filters.armedWith(Filters.weapon)));

        // Check condition(s)
        Collection<PhysicalCard> rebels = null;
        if (TriggerConditions.justDeployedTo(game, effectResult, rebelFilter, locationFilter)) {
            rebels = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, rebelFilter, locationFilter)) {
            rebels = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, rebelFilter);
        }
        if (rebels != null && !rebels.isEmpty()
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), rebels.iterator().next());

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target Rebel");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Rebel", Filters.in(rebels)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Prevent " + GameUtils.getCardLink(cardTargeted) + " from battling or moving and limit battle damage at " + GameUtils.getCardLink(site) + " to 4 until end of turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MayNotMoveOrBattleUntilEndOfTurnEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new BattleDamageLimitModifier(self, Filters.sameLocationId(site), 4, playerId),
                                                            "Limits battle damage to 4 at " + GameUtils.getCardLink(site)));
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