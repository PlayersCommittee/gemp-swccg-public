package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Noble Sacrifice
 */
public class Card1_099 extends AbstractLostInterrupt {
    public Card1_099() {
        super(Side.LIGHT, 3, Title.Noble_Sacrifice, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("'You can't win Darth. If you strike me down I shall become more powerful than you can possibly imagine.' Obi-Wan's sacrifice gave the Rebels time to escape.");
        setGameText("If opponent just deployed a character, sacrifice (out of play) from table one of your characters with the same power (even a captured character). You may retrieve Force from Lost Pile equal to your sacrificed character's forfeit value.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.and(Filters.character, Filters.canBeTargetedBy(self)))) {
            PhysicalCard justDeployedCharacter = ((PlayCardResult) effectResult).getPlayedCard();
            float power = game.getModifiersQuerying().getPower(game.getGameState(), justDeployedCharacter);
            final Filter characterToSacrificeFilter = Filters.and(Filters.your(self), Filters.character, Filters.powerEqualTo(power));
            final TargetingReason targetingReason = TargetingReason.TO_BE_PLACED_OUT_OF_PLAY;
            if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, targetingReason, characterToSacrificeFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Sacrifice a character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose deployed character", justDeployedCharacter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedDeployedCharacter) {
                                action.addAnimationGroup(targetedDeployedCharacter);
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character to sacrifice", SpotOverride.INCLUDE_CAPTIVE, targetingReason, characterToSacrificeFilter) {
                                            @Override
                                            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCharacterToSacrifice) {
                                                final boolean mayNotContributeToForceRetrieval = !Filters.mayContributeToForceRetrieval.accepts(game, targetedCharacterToSacrifice);
                                                final float forfeitValue = game.getModifiersQuerying().getForfeit(game.getGameState(), targetedCharacterToSacrifice);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PlaceCardOutOfPlayFromTableEffect(action, targetedCharacterToSacrifice));
                                                // Allow response(s)
                                                action.allowResponses("Retrieve " + GuiUtils.formatAsString(forfeitValue) + " Force by targeting " + GameUtils.getCardLink(targetedDeployedCharacter),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                if (mayNotContributeToForceRetrieval) {
                                                                    game.getGameState().sendMessage("Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval");
                                                                    return;
                                                                }
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new YesNoDecision("Do you want to retrieve " + GuiUtils.formatAsString(forfeitValue) + " Force?") {
                                                                                    @Override
                                                                                    protected void yes() {
                                                                                        game.getGameState().sendMessage(playerId + " chooses to retrieve " + GuiUtils.formatAsString(forfeitValue) + " Force");
                                                                                        action.appendEffect(
                                                                                                new RetrieveForceEffect(action, playerId, forfeitValue));
                                                                                    }

                                                                                    @Override
                                                                                    protected void no() {
                                                                                        game.getGameState().sendMessage(playerId + " chooses to not retrieve " + GuiUtils.formatAsString(forfeitValue) + " Force");
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}