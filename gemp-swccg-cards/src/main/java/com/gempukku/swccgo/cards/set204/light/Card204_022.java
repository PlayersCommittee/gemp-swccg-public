package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Garbage Will Do
 */
public class Card204_022 extends AbstractLostInterrupt {
    public Card204_022() {
        super(Side.LIGHT, 5, "The Garbage Will Do", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setGameText("Once per game, choose: During battle, place the top card of your Lost Pile out of play to add its destiny number to your total power. OR Peek at the cards stacked on Graveyard Of Giants; take (or steal) one starfighter or vehicle there into hand.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.THE_GARBAGE_WILL_DO__ADD_TO_TOTAL_POWER_OR_TAKE_CARD_FROM_GRAVEYARD_OF_GIANTS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            if (GameConditions.isDuringBattle(game)) {
                final PhysicalCard topCardOfLostPile = gameState.getTopOfLostPile(playerId);
                if (topCardOfLostPile != null) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Place top card of Lost Pile out of play");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Pay cost(s)
                    action.appendCost(
                            new RefreshPrintedDestinyValuesEffect(action, topCardOfLostPile) {
                                @Override
                                protected void refreshedPrintedDestinyValues() {
                                    float destinyValue = modifiersQuerying.getDestiny(gameState, topCardOfLostPile);
                                    action.setActionMsg("Add " + GuiUtils.formatAsString(destinyValue) + " to total power");
                                }
                            });
                    action.appendCost(
                            new PlaceCardOutOfPlayFromOffTableEffect(action, topCardOfLostPile));
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    float destinyValue = modifiersQuerying.getDestiny(gameState, topCardOfLostPile);
                                    // Perform result(s)
                                    action.appendEffect(
                                            new ModifyTotalPowerUntilEndOfBattleEffect(action, destinyValue, playerId,
                                                    "Adds " + GuiUtils.formatAsString(destinyValue) + " to total power"));
                                }
                            }
                    );
                    actions.add(action);
                }
            }

            final PhysicalCard graveyardOfGiants = Filters.findFirstActive(game, self, Filters.and(Filters.Graveyard_Of_Giants, Filters.hasStacked(Filters.any)));
            if (graveyardOfGiants != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Peek at cards on Graveyard Of Giants");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeStackedCardIntoHandEffect(action, playerId, graveyardOfGiants, Filters.and(Filters.or(Filters.starfighter, Filters.vehicle),
                                                Filters.or(Filters.your(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_STOLEN)))));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}