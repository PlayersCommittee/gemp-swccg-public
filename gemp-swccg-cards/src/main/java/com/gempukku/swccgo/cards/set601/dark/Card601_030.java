package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Block 8
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Wookiee Subjugation
 */
public class Card601_030 extends AbstractUsedOrStartingInterrupt {
    public Card601_030() {
        super(Side.DARK, 4, "Wookiee Subjugation", Uniqueness.UNIQUE);
        setLore("'Rrraaaarrr!'");
        setGameText("USED: If you just 'enslaved' a character, play a Defensive Shield from under your Starting Effect. STARTING: If Slaving Camp Headquarters on table, deploy from Reserve Deck Mercenary Slavers and up to two Effects that deploy on table for free and are always [Immune to Alter.]. Place Interrupt in Reserve Deck.");
        addIcons(Icon.BLOCK_8, Icon.DAGOBAH);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
//TODO        if (TriggerConditions.justEnslaved(game, effectResult, Filters.character)) {
        if (TriggerConditions.battleInitiated(game, effectResult)) {

            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                    action.setText("Play a Defensive Shield");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                @Override
                                protected void cardSelected(final PhysicalCard selectedCard) {
                                    // Allow response(s)
                                    action.allowResponses(
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                                }
                                            });
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.title("Kashyyyk: Slaving Camp Headquarters"))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy cards from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Mercenary Slavers and up to two Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title("Mercenary Slavers"), true));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 2, true, false));
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