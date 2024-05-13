package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: Accelerate (V)
 */
public class Card221_007 extends AbstractUsedInterrupt {
    public Card221_007() {
        super(Side.DARK, 6, "Accelerate", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire trains its personnel to operate a variety of specialized equipment in demanding environments. This training allows troops to take seemingly risky actions.");
        setGameText("For remainder of turn, your speeder bikes are power and forfeit +1. OR Subtract 2 from a just drawn destiny targeting the ability or defense value of a biker scout piloting a speeder bike.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canTarget(game, self, Filters.and(Filters.your(self), Filters.speeder_bike))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to power and forfeit of speeder bikes");

            // Allow response(s)
            action.allowResponses("Add 1 to power and forfeit of your speeder bikes",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, Filters.and(Filters.your(self), Filters.speeder_bike), 1), "Makes your speeder bikes power +1"));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.speeder_bike), 1), "Makes your speeder bikes forfeit +1"));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, Filters.and(Filters.biker_scout, Filters.piloting(Filters.speeder_bike)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Subtract 2 from destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -2));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}