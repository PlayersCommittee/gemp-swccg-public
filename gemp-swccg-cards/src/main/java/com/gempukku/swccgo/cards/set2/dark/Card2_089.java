package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Greedo
 */
public class Card2_089 extends AbstractAlien {
    public Card2_089() {
        super(Side.DARK, 2, 1, 2, 1, 0.5, Title.Greedo, Uniqueness.UNIQUE);
        setLore("Male Rodian bounty hunter. Sent by Jabba to capture Han. Arrogant, overconfident and not too bright. Trained by bounty hunters Nataz and Goa, who betrayed him to Thuku.");
        setGameText("During opponent's control phase, may 'threaten' one smuggler at same site. Opponent may use all Force Pile cards to cancel threat, allowing you to activate same amount. Otherwise, draw destiny. If destiny + Greedo's ability > 6, smuggler lost.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.RODIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.smuggler, Filters.atSameSite(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasForcePile(game, opponent)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Threaten' smuggler");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target smuggler", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("'Threaten' " + GameUtils.getCardLink(cardTargeted),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard smuggler = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, opponent,
                                                            new YesNoDecision("Do you want to cancel threat by using all Force Pile cards?") {
                                                                @Override
                                                                protected void yes() {
                                                                    game.getGameState().sendMessage(opponent + " chooses to cancel threat");
                                                                    final int amountOfForce = game.getGameState().getForcePileSize(opponent);
                                                                    if (amountOfForce > 0) {
                                                                        action.appendEffect(
                                                                                new UseForceEffect(action, opponent, amountOfForce, true));

                                                                        if (GameConditions.canActivateForce(game, playerId)) {
                                                                            action.appendEffect(
                                                                                    new PlayoutDecisionEffect(action, playerId,
                                                                                            new YesNoDecision("Do you want to activate " + amountOfForce + " Force?") {
                                                                                                @Override
                                                                                                protected void yes() {
                                                                                                    action.appendEffect(
                                                                                                            new ActivateForceEffect(action, playerId, amountOfForce));
                                                                                                }

                                                                                                @Override
                                                                                                protected void no() {
                                                                                                    game.getGameState().sendMessage(playerId + " chooses to not activate Force");
                                                                                                }
                                                                                            }
                                                                                    )
                                                                            );
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(opponent + " chooses to not cancel threat");
                                                                    action.appendEffect(
                                                                            new DrawDestinyEffect(action, playerId, 1, DestinyType.THREATEN_DESTINY) {
                                                                                @Override
                                                                                protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                                    return Collections.singletonList(self);
                                                                                }
                                                                                @Override
                                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                    GameState gameState = game.getGameState();
                                                                                    if (totalDestiny == null) {
                                                                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                        return;
                                                                                    }

                                                                                    float ability = game.getModifiersQuerying().getAbility(game.getGameState(), self);
                                                                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                                    gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));

                                                                                    if ((totalDestiny + ability) > 6) {
                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                        action.appendEffect(
                                                                                                new LoseCardFromTableEffect(action, smuggler));
                                                                                    }
                                                                                    else {
                                                                                        gameState.sendMessage("Result: Failed");
                                                                                    }
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }));
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
