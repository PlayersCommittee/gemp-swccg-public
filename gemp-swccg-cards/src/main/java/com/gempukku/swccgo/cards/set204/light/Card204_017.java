package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Lost
 * Title: Are You Okay?
 */
public class Card204_017 extends AbstractUsedInterrupt {
    public Card204_017() {
        super(Side.LIGHT, 4, "Are You Okay?", Uniqueness.UNIQUE);
        setGameText("Subtract 2 from a just drawn destiny targeting the ability or defense value of your non-Jedi [Episode VII] character. OR If you just forfeited a character from a Jakku site during your turn, use 1 Force to deploy that character from Lost Pile to same site for free.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult,
                Filters.and(Filters.your(self), Filters.non_Jedi_character, Icon.EPISODE_VII))) {

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

        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.character), Filters.Jakku_site)
                && GameConditions.isDuringYourTurn(game, self)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            final PhysicalCard cardLost = lostFromTableResult.getCard();
            final PhysicalCard location = lostFromTableResult.getFromLocation();
            if (Filters.deployableToLocation(self, Filters.sameCardId(location), true, 0).accepts(game, cardLost)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy " + GameUtils.getFullName(cardLost) + " from Lost Pile");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Allow response(s)
                action.allowResponses("Deploy " + GameUtils.getCardLink(cardLost) + " from Lost Pile",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToLocationFromLostPileEffect(action, cardLost, Filters.sameLocation(location), true, false));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}