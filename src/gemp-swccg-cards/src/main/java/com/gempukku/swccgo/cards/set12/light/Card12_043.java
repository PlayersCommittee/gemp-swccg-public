package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Political
 * Title: I Will Not Defer
 */
public class Card12_043 extends AbstractPoliticalEffect {
    public Card12_043() {
        super(Side.LIGHT, 3, "I Will Not Defer", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("'I've come before you to resolve this attack on our sovereignty now.'");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to add 2 to your next Force drain at a battleground this turn. If a peace agenda here, once per turn you may activate up to 2 Force when opponent initiates battle.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.hasStackedCards(game, self, Filters.senator)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 2 to your next battleground Force drain");
            action.setActionMsg("Add 2 to " + playerId + "'s next Force drain at a battleground this turn");
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect)));
            // Perform result(s)
            action.appendEffect(
                    new ClearForRemainderOfGameDataEffect(action, self, true));
            final int permCardId = self.getPermanentCardId();
            action.appendEffect(
                    new AddUntilEndOfTurnActionProxyEffect(action,
                            new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                    // Check condition(s)
                                    if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.battleground)
                                            && !GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
                                        self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
                                        // Add modifier here without creating an action
                                        game.getModifiersEnvironment().addUntilEndOfForceDrainModifier(
                                                new ForceDrainModifier(self, Filters.forceDrainLocation, 2, playerId));
                                    }
                                    return null;
                                }
                            }
                    ));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, Filters.peace_agenda)
                && GameConditions.canActivateForce(game, playerId)) {
            int maxForceToActivate = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxForceToActivate > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Activate up to 2 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Activate " + result + " Force");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}