package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Hidden Weapons
 */
public class Card6_154 extends AbstractUsedInterrupt {
    public Card6_154() {
        super(Side.DARK, 2, Title.Hidden_Weapons, Uniqueness.UNIQUE);
        setLore("Boba Fett's Mandalorian armor was so versatile that his opponents never knew what to expect.");
        setGameText("If Boba Fett, or your character with Mandalorian Armor is present during the weapons phase of a battle, target one opponent's character present. Draw Destiny: (0-1) no effect (2-3) character immediately captured (4-5) character is hit (6+) character immediately lost.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            Filter filter = Filters.and(Filters.or(Filters.Boba_Fett, Filters.grantedMayBeTargetedBy(self), Filters.and(Filters.your(self),
                    Filters.character, Filters.hasAttached(Filters.Mandalorian_Armor))), Filters.presentInBattle);
            if (GameConditions.canSpot(game, self, filter)) {
                Filter opponentsCharacter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
                final Set<TargetingReason> targetingReasonSet = new HashSet<TargetingReason>();
                targetingReasonSet.add(TargetingReason.TO_BE_CAPTURED);
                targetingReasonSet.add(TargetingReason.TO_BE_HIT);
                targetingReasonSet.add(TargetingReason.TO_BE_LOST);
                if (GameConditions.canTarget(game, self, targetingReasonSet, opponentsCharacter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReasonSet, opponentsCharacter) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Allow response(s)
                                    action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
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

                                                                    if (totalDestiny >= 2 && totalDestiny <= 3) {
                                                                        gameState.sendMessage("Result: Target captured");
                                                                        action.appendEffect(
                                                                                new CaptureCharacterOnTableEffect(action, finalTarget));
                                                                    }
                                                                    else if (totalDestiny >= 4 && totalDestiny <= 5) {
                                                                        gameState.sendMessage("Result: Target 'hit'");
                                                                        action.appendEffect(
                                                                                new HitCardEffect(action, finalTarget, self));
                                                                    }
                                                                    else if (totalDestiny >= 6) {
                                                                        gameState.sendMessage("Result: Target lost");
                                                                        action.appendEffect(
                                                                                new LoseCardFromTableEffect(action, finalTarget));
                                                                    }
                                                                    else {
                                                                        gameState.sendMessage("Result: No effect");
                                                                    }
                                                               }
                                                            });
                                                }
                                            });
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}