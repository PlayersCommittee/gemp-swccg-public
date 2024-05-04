package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Trade Federation Tactics
 */
public class Card14_109 extends AbstractLostInterrupt {
    public Card14_109() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.");
        setGameText("Use 2 Force to target a starship at same system as your [Trade Federation] starship armed with a weapon. Target is forfeit = 0 until end of turn. OR During battle, add X to your total power, where X = armor of one of your [Trade Federation] starships in that battle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.and(Filters.starship, Filters.at(Filters.sameSystemAs(self, Filters.and(Filters.your(self), Icon.TRADE_FEDERATION, Filters.starship, Filters.armedWith(Filters.weapon)))));

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reset starship's forfeit to 0");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Reset " + GameUtils.getCardLink(starship) + "'s forfeit to 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetForfeitUntilEndOfTurnEffect(action, finalTarget, 0));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter2 = Filters.and(Filters.your(self), Icon.TRADE_FEDERATION, Filters.starship, Filters.hasArmor, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add to total power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", filter2) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            final float armor = game.getModifiersQuerying().getArmor(game.getGameState(), starship);
                            // Allow response(s)
                            action.allowResponses("Add " + GameUtils.getCardLink(starship) + "'s armor of " + GuiUtils.formatAsString(armor) + " to total power",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, armor, playerId,
                                                            "Adds " + GuiUtils.formatAsString(armor) + " to total power"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}