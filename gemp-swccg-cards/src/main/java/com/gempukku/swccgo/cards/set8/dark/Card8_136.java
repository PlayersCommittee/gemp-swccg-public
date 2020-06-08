package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Combat Readiness
 */
public class Card8_136 extends AbstractUsedOrStartingInterrupt {
    public Card8_136() {
        super(Side.DARK, 5, Title.Combat_Readiness, Uniqueness.UNIQUE);
        setLore("Imperial troops prepared for quick deployment to seize valuable terrain.");
        setGameText("USED: If a battle was just initiated, draw destiny and activate up to that much Force. STARTING: If you have not deployed an Objective, deploy from Reserve Deck one battleground site (or two ◇ sites) related to your starting location. Place Interrupt in Reserve Deck.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Draw destiny to activate Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null && totalDestiny > 0) {
                                                int maxForceForPlayerToActivate = Math.min((int) Math.floor(totalDestiny), game.getGameState().getReserveDeckSize(playerId));
                                                if (maxForceForPlayerToActivate > 0) {
                                                    action.appendEffect(
                                                            new PlayoutDecisionEffect(action, playerId,
                                                                    new IntegerAwaitingDecision("Choose amount of Force to activate", 1, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                                                        @Override
                                                                        public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                            // Perform result(s)
                                                                            action.appendEffect(
                                                                                    new ActivateForceEffect(action, playerId, result));
                                                                        }
                                                                    }
                                                            )
                                                    );
                                                }
                                            }
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

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.didNotDeployAnObjective(game, playerId)) {
            PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
            if (startingLocation != null) {
                final String systemName = startingLocation.getPartOfSystem();
                if (systemName != null) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
                    action.setText("Deploy sites from Reserve Deck");
                    // Allow response(s)
                    action.allowResponses("Deploy one battleground site (or two ◇ sites) related to your starting location from Reserve Deck",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, systemName, Filters.battleground, true, false) {
                                                @Override
                                                protected void cardDeployed(PhysicalCard card) {
                                                    Filter genericSite = Filters.and(Filters.generic, Filters.site);
                                                    if (genericSite.accepts(game, card)) {
                                                        action.insertEffect(
                                                                new DeployCardsToSystemFromReserveDeckEffect(action, playerId, genericSite, 0, 1, systemName, Filters.battleground, true, false));
                                                    }
                                                }
                                            }
                                    );
                                    action.appendEffect(
                                            new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                                }
                            }
                    );
                    return action;
                }
            }
        }
        return null;
    }
}