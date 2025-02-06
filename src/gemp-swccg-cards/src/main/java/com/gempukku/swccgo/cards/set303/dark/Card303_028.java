package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerAndAttritionEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfPlayersNextTurnEffect;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Gentle Vistage
 */
public class Card303_028 extends AbstractUsedOrLostInterrupt {
    public Card303_028() {
        super(Side.DARK, 5, "Gentle Vistage", Uniqueness.UNRESTRICTED, ExpansionSet.SA, Rarity.U);
        setLore("Not everyone who is evil wears a mask.");
        setGameText("USED: Cancel Dark Side Corruption or cancel an attempt to target a Dark Jedi with a character weapon. LOST: Use 2 Force to target a character present with your Dark Jedi. Target cannot move or battle until end of your next turn.");
    }

	@Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.Dark_Jedi, Filters.character_weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Dark_Side_Corruption)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
	
	@Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            Filter filter = Filters.and(Filters.character, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Dark_Jedi)));
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Prevent character from moving or battling");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 2));
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getCardLink(targetedCard) + " from battling or moving until end of " + playerId + "'s next turn",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MayNotMoveOrBattleUntilEndOfPlayersNextTurnEffect(action, finalTarget, playerId));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
		return actions;
	}
}


    