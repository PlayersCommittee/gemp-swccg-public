package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Let The Wookiee Win (V)
 */
public class Card215_016 extends AbstractUsedOrStartingInterrupt {
    public Card215_016() {
        super(Side.LIGHT, 5, Title.Let_The_Wookiee_Win, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'It's not wise to upset a Wookiee.' 'But sir, nobody worries about upsetting a droid.' 'That's cause a droid don't pull people's arms out of their sockets when they lose.'");
        setGameText("USED: Subtract 2 from a just drawn destiny targeting your Wookiee's ability or defense value. " +
                "STARTING: If your starting location had exactly two [Light Side], deploy a Kashyyyk location and three Effects that are always immune to Alter. Place Interrupt in Reserve Deck.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult,
                Filters.and(Filters.your(self), Filters.Wookiee))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
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

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && Filters.iconCount(Icon.LIGHT_FORCE, 2).accepts(game, startingLocation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy a Kashyyyk location and Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy a Kashyyyk location and three Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.Kashyyyk_location, 1, 1, true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 3, 3, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }

        return null;
    }
}