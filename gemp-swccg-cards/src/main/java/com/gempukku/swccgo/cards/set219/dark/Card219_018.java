package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfLightsaberCombatModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.NumLightsaberCombatDestinyDrawsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Surely You Can Do Better
 */
public class Card219_018 extends AbstractUsedOrLostInterrupt {
    public Card219_018() {
        super(Side.DARK, 4, "Surely You Can Do Better", Uniqueness.UNIQUE);
        setGameText("USED: During battle, target a character present with [Set 13] Dooku. Target is power -3. " +
                    "LOST: If lightsaber combat was just initiated, add one destiny to your total. " +
                    "OR If [Set 13] Dooku on table, deploy Dooku’s Lightsaber from Lost Pile.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        Filter set13Dooku = Filters.and(Icon.VIRTUAL_SET_13, Filters.Dooku);
        Filter targetFilter = Filters.and(Filters.character, Filters.presentWith(self, set13Dooku), Filters.canBeTargetedBy(self));
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reduce power of a character");
            action.setActionMsg("Make a character present with [Set 13] Dooku power -3");
            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a character to be power -3", targetFilter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                    // Perform result(s)
                                    action.appendEffect(
                                            new ModifyPowerUntilEndOfBattleEffect(action, finalTarget, -3));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }

        // OR If [Set 13] Dooku on table, deploy Dooku’s Lightsaber from Lost Pile
        if (GameConditions.canSpot(game, self, set13Dooku)
                && GameConditions.hasLostPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Deploy Dooku’s Lightsaber from Lost Pile");
            action.setActionMsg("Deploy Dooku’s Lightsaber from Lost Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromLostPileEffect(action, Filters.title("Dooku's Lightsaber"), false)
                            );
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.lightsaberCombatInitiated(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add one destiny");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfLightsaberCombatModifierEffect(action,
                                            new NumLightsaberCombatDestinyDrawsModifier(self, 1, playerId),
                                            "Add destiny"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}