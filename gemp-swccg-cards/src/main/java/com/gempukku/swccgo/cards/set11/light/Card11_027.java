package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfLostPileEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDuelTotalEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Jedi's Focus
 */
public class Card11_027 extends AbstractUsedOrLostInterrupt {
    public Card11_027() {
        super(Side.LIGHT, 4, "A Jedi's Focus", Uniqueness.UNIQUE);
        setLore("Vader was surprised at how far the 'young apprentice' had come in his training.");
        setGameText("USED: If you have a character with a lightsaber in battle, all opponent's aliens at same site are power -1 for remainder of turn (-2 if alien is non-unique). LOST: If a duel was just initiated, add the destiny number of the top card of your Lost Pile to your total.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter alienFilter = Filters.and(Filters.opponents(self), Filters.alien, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber))
                && GameConditions.isDuringBattleWithParticipant(game, alienFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reduce power of opponent's aliens");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> aliens = Filters.filterActive(game, self, alienFilter);
                            if (!aliens.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(aliens), new CardMatchesEvaluator(-1, -2, Filters.non_unique)),
                                                "Makes " + GameUtils.getAppendedNames(aliens) + " power -1 (or -2 if non-unique)"));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isDuelAddOrModifyDuelDestiniesStep(game, effectResult)
                && GameConditions.hasLostPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add destiny of top card of Lost Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final GameState gameState = game.getGameState();
                            // Perform result(s)
                            if (GameConditions.isLostPileTurnedOver(game, playerId)) {
                                action.appendEffect(
                                        new RevealTopCardOfLostPileEffect(action, playerId) {
                                            @Override
                                            protected void cardRevealed(final PhysicalCard topLostCard) {
                                                action.appendEffect(
                                                        new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(topLostCard)) {
                                                            @Override
                                                            protected void refreshedPrintedDestinyValues() {
                                                                float destinyNumber = game.getModifiersQuerying().getDestiny(gameState, topLostCard);
                                                                gameState.sendMessage("Destiny number: " + GuiUtils.formatAsString(destinyNumber));

                                                                action.appendEffect(
                                                                        new ModifyDuelTotalEffect(action, destinyNumber, playerId,
                                                                                "Adds " + GuiUtils.formatAsString(destinyNumber) + " to duel total"));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                            else {
                                final PhysicalCard topLostCard = gameState.getTopOfLostPile(playerId);
                                if (topLostCard != null) {
                                    action.appendEffect(
                                            new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(topLostCard)) {
                                                @Override
                                                protected void refreshedPrintedDestinyValues() {
                                                    float destinyNumber = game.getModifiersQuerying().getDestiny(gameState, topLostCard);
                                                    gameState.sendMessage("Destiny number: " + GuiUtils.formatAsString(destinyNumber));

                                                    action.appendEffect(
                                                            new ModifyDuelTotalEffect(action, destinyNumber, playerId,
                                                                    "Adds " + GuiUtils.formatAsString(destinyNumber) + " to duel total"));
                                                }
                                            }
                                    );
                                }
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}