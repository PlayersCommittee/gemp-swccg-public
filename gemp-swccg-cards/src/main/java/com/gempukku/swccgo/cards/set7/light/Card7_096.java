package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.PermanentPilotsMayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: On Target
 */
public class Card7_096 extends AbstractUsedInterrupt {
    public Card7_096() {
        super(Side.LIGHT, 5, Title.On_Target, Uniqueness.UNIQUE);
        setLore("Some Imperial starfighters are equipped with sensors, informing the pilot when an enemy craft has targeted him. The pilot's evasive maneuvers cost him time, but save his life.");
        setGameText("If you have a piloted capital starship armed with a starship weapon, use 2 Force to target an opponent's starship present. Until the end of your next turn, target cannot move and its pilots may not apply ability toward drawing battle destiny.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.opponents(self), Filters.starship, Filters.presentAt(Filters.sameLocationAs(self,
                Filters.and(Filters.your(self), Filters.piloted, Filters.capital_starship, Filters.armedWith(Filters.starship_weapon)))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target starship");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(starship),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                                            new MayNotMoveModifier(self, finalTarget),
                                                            "Causes " + GameUtils.getCardLink(finalTarget) + " to not move"));
                                            action.appendEffect(
                                                    new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                                            new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.piloting(finalTarget)),
                                                            "Causes " + GameUtils.getCardLink(finalTarget) + "'s pilots to not apply ability for battle destiny"));
                                            action.appendEffect(
                                                    new AddUntilEndOfPlayersNextTurnModifierEffect(action, playerId,
                                                            new PermanentPilotsMayNotApplyAbilityForBattleDestinyModifier(self, finalTarget), null));
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