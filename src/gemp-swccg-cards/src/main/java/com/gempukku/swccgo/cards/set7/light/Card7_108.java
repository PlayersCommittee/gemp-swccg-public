package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: They're Tracking Us
 */
public class Card7_108 extends AbstractLostInterrupt {
    public Card7_108() {
        super(Side.LIGHT, 4, "They're Tracking Us", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("'Not this ship, sister.'");
        setGameText("If opponent just deployed a planet site, deploy the related system from your Reserve Deck; reshuffle. OR Cancel one opponent's 'react' unless opponent uses 2 additional Force.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.THEYRE_TRACKING_US__DOWNLOAD_RELATED_SYSTEM;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.planet_site)) {
            PhysicalCard planetSite = ((PlayCardResult) effectResult).getPlayedCard();
            final String systemName = planetSite.getPartOfSystem();
            if (systemName != null
                    && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, systemName)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy " + systemName + " system from Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.system, Filters.title(systemName)), true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isReactJustInitiatedBy(game, effect, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel 'react'"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to allow 'react' to be canceled");
                                                            action.appendEffect(
                                                                    new CancelReactEffect(action));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action.appendEffect(
                                        new CancelReactEffect(action));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}