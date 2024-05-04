package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Too Close For Comfort
 */
public class Card11_040 extends AbstractUsedOrLostInterrupt {
    public Card11_040() {
        super(Side.LIGHT, 5, "Too Close For Comfort", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Anakin needed all the help he could get to evade Sebulba's tactics.");
        setGameText("USED: If your character was just targeted by a weapon, opponent must use 2 Force or the targeting is canceled. LOST: Cancel Hit Racer or Podracer Collision.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character), Filters.weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Make opponent use 2 Force or cancel targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel targeting"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to cancel targeting");
                                                            action.appendEffect(
                                                                    new CancelWeaponTargetingEffect(action));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action.appendEffect(
                                        new CancelWeaponTargetingEffect(action));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter = Filters.or(Filters.Hit_Racer, Filters.Podracer_Collision);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Hit_Racer)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Hit_Racer, Title.Hit_Racer);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Podracer_Collision)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Podracer_Collision, Title.Podracer_Collision);
            actions.add(action);
        }
        return actions;
    }
}