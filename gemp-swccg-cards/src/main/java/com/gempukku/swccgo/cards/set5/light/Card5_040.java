package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interupt
 * Subtype: Used or Lost
 * Title: Computer Interface
 */
public class Card5_040 extends AbstractUsedOrLostInterrupt {
    public Card5_040() {
        super(Side.LIGHT, 3, Title.Computer_Interface);
        setLore("");
        setGameText("USED: Cancel Limited Resources. LOST: Use 2 Force (free if Lobot on table) to examine the top card of your Reserve Deck, Force Pile and Used Pile. Return one of those three cards to the top of each deck or pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Limited_Resources)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canTargetToCancel(game, self, Filters.Limited_Resources)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Limited_Resources, Title.Limited_Resources);
            actions.add(action);
        }

        final int forceToUse;
        if(GameConditions.canSpot(game, self, Filters.Lobot)){
            forceToUse = 0;
        }else{
            forceToUse = 2;
        }

        if (GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasUsedPile(game, playerId)
                && GameConditions.canUseForce(game, playerId, forceToUse)){
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Peek at top card of each pile");
            //Allow Responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Update usage limit(s)
                            action.appendEffect(
                                        new UseForceEffect(action, playerId, forceToUse));

                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect(action, playerId));
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }
}