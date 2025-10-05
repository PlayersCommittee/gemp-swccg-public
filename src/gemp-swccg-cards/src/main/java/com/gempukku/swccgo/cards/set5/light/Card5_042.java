package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dark Approach
 */
public class Card5_042 extends AbstractLostInterrupt {
    public Card5_042() {
        super(Side.LIGHT, 4, Title.Dark_Approach, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("'The Force is with you, young Skywalker. But you are not a Jedi yet.'");
        setGameText("If opponent just initiated a battle, you may do one of the following: Add one destiny to power only. OR If you have a character weapon present, select one opponent's character present to lose all immunity to attrition for remainder of battle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one destiny to total power");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddDestinyToTotalPowerEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }

            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.hasAnyImmunityToAttrition, Filters.presentInBattle,
                    Filters.presentWith(self, Filters.and(Filters.your(self), Filters.character_weapon)));
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make character lose immunity to attrition");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(character) + " lose immunity to attrition",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelImmunityToAttritionUntilEndOfTurnEffect(action, finalCharacter,
                                                                "Cancels " + GameUtils.getCardLink(finalCharacter) + "'s immunity to attrition"));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}