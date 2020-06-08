package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Strangle
 */
public class Card6_076 extends AbstractLostInterrupt {
    public Card6_076() {
        super(Side.LIGHT, 7, Title.Strangle, Uniqueness.UNIQUE);
        setLore("'Aacccck!'");
        setGameText("Target an escort alone and its captive. Draw destiny. Escort lost if destiny + captive's power > escort's ability + power. OR During a battle at a site where Leia is an escorted captive, add one battle destiny.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.escorting(Filters.nonFrozenCaptive), Filters.alone);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target an escort alone");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose escort", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard escort) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, Filters.escortedBy(escort)) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard captive) {
                                            action.addAnimationGroup(escort, captive);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(escort) + " and " + GameUtils.getCardLink(captive),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalEscort = action.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalCaptive = action.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Collections.singletonList(finalEscort);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();

                                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                            float captivesPower = modifiersQuerying.getPower(gameState, finalCaptive);
                                                                            gameState.sendMessage("Captive's power: " + GuiUtils.formatAsString(captivesPower));
                                                                            float escortsAbility = modifiersQuerying.getAbility(gameState, finalEscort);
                                                                            gameState.sendMessage("Escort's ability: " + GuiUtils.formatAsString(escortsAbility));
                                                                            float escortsPower = modifiersQuerying.getPower(gameState, finalEscort);
                                                                            gameState.sendMessage("Escort's power: " + escortsPower);

                                                                            if (((totalDestiny != null ? totalDestiny : 0) + captivesPower) > (escortsAbility + escortsPower)) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalEscort));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
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
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Leia, Filters.escortedBy(self, Filters.participatingInBattle)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}