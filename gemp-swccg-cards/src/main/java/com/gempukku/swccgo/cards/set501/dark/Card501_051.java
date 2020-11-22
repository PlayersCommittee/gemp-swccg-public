package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Eee Chu Wawa! (V)
 */
public class Card501_051 extends AbstractUsedOrLostInterrupt {
    public Card501_051() {
        super(Side.DARK, 4, Title.Eee_Chu_Wawa, Uniqueness.UNIQUE);
        setLore("Paploo's brave diversion provided more of a ride than the adventurous Ewok had bargained for.");
        setGameText("USED: If opponent occupies your location (or if a forest on table), peek at top two cards of your Reserve Deck; take one into hand." +
                "LOST: While defending a battle, add 1 to your total battle destiny for each (DS icon) at same site. OR Cancel Take The Initiative.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_14);
        setVirtualSuffix(true);
        setTestingText("Eee Chu Wawa! (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Take_The_Initiative)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if ((GameConditions.canSpotLocation(game, Filters.and(Filters.your(self), Filters.location, Filters.occupies(opponent)))
                || GameConditions.canSpotLocation(game, Filters.forest))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Peek at top two cards of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Peek at top two cards of Reserve Deck and take one into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if ((GameConditions.isDuringBattleInitiatedBy(game, opponent))
                && GameConditions.isDuringBattleAt(game, Filters.site)) {
            PhysicalCard battleLocation = Filters.findFirstActive(game, self, Filters.battleLocation);
            final float dsIcons = battleLocation.getBlueprint().getIconCount(Icon.DARK_FORCE);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add " + dsIcons + " to you total battle destiny");
            // Allow response(s)
            action.allowResponses("Add " + dsIcons + " to you total battle destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyTotalBattleDestinyEffect(action, playerId, dsIcons)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Take_The_Initiative)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Take_The_Initiative, Title.Take_The_Initiative);
            actions.add(action);
        }

        return actions;
    }
}