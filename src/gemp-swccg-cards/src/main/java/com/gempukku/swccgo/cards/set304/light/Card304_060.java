package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Sqygorn Dar
 */
public class Card304_060 extends AbstractAlien {
    public Card304_060() {
        super(Side.LIGHT, 3, 4, 3, 3, 4, "Sqygorn Dar", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Sqygorn serves as Candon Coburn's gangster enforcer. whenever there's a job that Candon wants done cleanly and quickly, Sqygorn's who he calls. Sqygorn, however, is just in it for the spice. Leader.");
		setGameText("During opponent's control phase, may 'threaten' one [CSP] at same site. Opponent may use all Force Pile cards to cancel threat, allowing you to activate same amount. Otherwise, draw destiny. If destiny + Sqygorn's ability > 6, [CSP] lost.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER);
        addPersona(Persona.SQYGORN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.CSP, Filters.atSameSite(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasForcePile(game, opponent)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Threaten' CSP member");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target CSP member", targetingReason, targetFilter) {
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
                                            final PhysicalCard CSP = targetingAction.getPrimaryTargetCard(targetGroupId);

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
                                                                                                new LoseCardFromTableEffect(action, CSP));
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
