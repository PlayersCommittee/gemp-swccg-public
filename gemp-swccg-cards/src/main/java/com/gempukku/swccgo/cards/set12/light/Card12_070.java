package com.gempukku.swccgo.cards.set12.light;

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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Stay Here, Where It's Safe
 */
public class Card12_070 extends AbstractUsedInterrupt {
    public Card12_070() {
        super(Side.LIGHT, 4, "Stay Here, Where It's Safe", Uniqueness.UNIQUE);
        setLore("Having enforced a momentous change upon the Senate, the headstrong Queen then wished to return to Naboo and do the same.");
        setGameText("Target your character with a peace or order agenda. Draw destiny. For remainder of turn, target is power +X (or politics +X if at Galactic Senate) and immune to attrition < X, where X = destiny number of the card drawn. (Immune to Sense.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.or(Filters.peace_agenda, Filters.order_agenda));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, characterFilter)
                && GameConditions.canDrawDestiny(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target character");
            action.setImmuneTo(Title.Sense);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(character),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float valueForX = totalDestiny;
                                                            gameState.sendMessage("Value for X: " + GuiUtils.formatAsString(valueForX));
                                                            if (valueForX > 0) {
                                                                // Perform result(s)
                                                                if (Filters.at(Filters.Galactic_Senate).accepts(game, finalTarget)) {
                                                                    action.appendEffect(
                                                                            new ModifyPoliticsUntilEndOfTurnEffect(action, finalTarget, valueForX,
                                                                                    "Adds " + GuiUtils.formatAsString(valueForX) + " to " + GameUtils.getCardLink(finalTarget) + "'s politics"));
                                                                }
                                                                else {
                                                                    action.appendEffect(
                                                                            new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, valueForX));
                                                                }
                                                                action.appendEffect(
                                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                                new ImmuneToAttritionLessThanModifier(self, finalTarget, valueForX),
                                                                                "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition < " + GuiUtils.formatAsString(valueForX)));
                                                            }
                                                        }
                                                    }
                                            );
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