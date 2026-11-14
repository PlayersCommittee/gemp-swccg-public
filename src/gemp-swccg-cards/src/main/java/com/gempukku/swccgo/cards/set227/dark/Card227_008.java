package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Interrupt
 * Subtype: Used
 * Title: Dark Lightning
 */
public class Card227_008 extends AbstractUsedInterrupt {
    public Card227_008() {
        super(Side.DARK, 6, "Dark Lightning", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setGameText("Target a character present with your Dark Jedi Master. Draw destiny. If destiny + 2 > target's ability, target's game text is canceled for remainder of turn and, if target is Anakin, Luke, or Rey, opponent loses 1 Force (2 Force if a [Skywalker] Epic Event is also on table).");
        addIcons(Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter targetFilter = Filters.and(Filters.character,
            Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Dark_Jedi_Master)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel a character's game text");
            // Choose target(s)
            action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Draw destiny to cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
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
                                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                return Collections.singletonList(finalTarget);
                                            }
                                            @Override
                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                GameState gameState = game.getGameState();

                                                float ability = game.getModifiersQuerying().getAbility(gameState, finalTarget);

                                                if (totalDestiny != null) {
                                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                    gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                    if ((totalDestiny + 2) > ability) {
                                                        gameState.sendMessage("Result: Succeeded");
                                                        action.appendEffect(
                                                            new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                                        if (Filters.or(Filters.Anakin, Filters.Luke, Filters.Rey).accepts(game, finalTarget) && Filters.canSpot(game, self, Filters.and(Icon.SKYWALKER, Filters.Epic_Event))) {
                                                            action.appendEffect(
                                                                new LoseForceEffect(action, game.getOpponent(playerId), 2, true));
                                                        }
                                                        else if (Filters.or(Filters.Anakin, Filters.Luke, Filters.Rey).accepts(game, finalTarget)) {
                                                            action.appendEffect(
                                                                new LoseForceEffect(action, game.getOpponent(playerId), 1, true));
                                                        }
                                                    } else {
                                                        gameState.sendMessage("Result: Failed");
                                                    }
                                                } else {
                                                    gameState.sendMessage("Result: Failed Destiny Draw.");
                                                }
                                            }
                                        });
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
