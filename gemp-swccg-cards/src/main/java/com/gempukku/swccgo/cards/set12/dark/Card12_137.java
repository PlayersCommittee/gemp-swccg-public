package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Political
 * Title: Motion Supported
 */
public class Card12_137 extends AbstractPoliticalEffect {
    public Card12_137() {
        super(Side.DARK, 3, "Motion Supported", Uniqueness.UNIQUE);
        setLore("'The delegates from Malastare concur the with delegates from the Trade Federation. A commission must be appointed!'");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to add 2 to your next Force drain at a battleground this turn. If a wealth agenda here, once per turn, you may lose your just drawn battle destiny to substitute it with a card from hand.");
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
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, Filters.wealth_agenda)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.canMakeDestinyCardLost(game)
                && GameConditions.hasHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel and substitute destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(selectedCard)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            final float destinyValue = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                                            action.setActionMsg("Cancel and lose just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", and substitute from hand " + GameUtils.getCardLink(selectedCard) + "'s destiny value of " + GuiUtils.formatAsString(destinyValue) + " for battle destiny");
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseDestinyCardEffect(action));
                                            action.appendEffect(
                                                    new PutCardFromHandOnUsedPileEffect(action, playerId, selectedCard, false));
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, destinyValue));
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