package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Overload
 */
public class Card1_263 extends AbstractLostInterrupt {
    public Card1_263() {
        super(Side.DARK, 4, Title.Overload, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Weapons like lightsabers, turbolasers and blasters run on powerful energy cells or generators. Occasionally, these cells overheat causing the weapon to unexpectedly explode.");
        setGameText("Target any weapon (except a Gaderffii Stick or any Ewok weapon). Draw destiny. Weapon lost if destiny < weapon's destiny number. If destiny = 0, the character or starship carrying weapon is also lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.weapon_or_character_with_permanent_weapon, Filters.except(Filters.or(Filters.Gaderffii_Stick, Filters.Ewok_weapon)));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose weapon", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
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
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(finalTarget)) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                                            // Card with permanent weapon only lost if destiny = 0
                                                                            if (Filters.weapon.accepts(game, finalTarget)) {
                                                                                float weaponsDestiny = modifiersQuerying.getDestiny(gameState, finalTarget);
                                                                                gameState.sendMessage("Weapon's destiny: " + GuiUtils.formatAsString(weaponsDestiny));

                                                                                if (totalDestiny < weaponsDestiny) {
                                                                                    gameState.sendMessage("Result: Succeeded");
                                                                                    if (totalDestiny == 0
                                                                                            && finalTarget != null
                                                                                            && Filters.attachedTo(Filters.or(Filters.character, Filters.starship)).accepts(game, finalTarget)) {
                                                                                        action.appendEffect(
                                                                                                new LoseCardsFromTableEffect(action, Arrays.asList(finalTarget, finalTarget.getAttachedTo())));
                                                                                    }
                                                                                    else {
                                                                                        action.appendEffect(
                                                                                                new LoseCardFromTableEffect(action, finalTarget));
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    gameState.sendMessage("Result: Failed");
                                                                                }
                                                                            }
                                                                            else {
                                                                                if (totalDestiny == 0) {
                                                                                    gameState.sendMessage("Result: Succeeded");
                                                                                    action.appendEffect(
                                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                                }
                                                                                else {
                                                                                    gameState.sendMessage("Result: Failed");
                                                                                }
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
            return Collections.singletonList(action);
        }
        return null;
    }
}