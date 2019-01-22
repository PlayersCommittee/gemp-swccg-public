package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.OccupiesWithEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect.exchangeCardInHandWIthCardOfSameTypeInLostPile;

/**
 * Set: Set 10
 * Type: Objective
 * Title: Ralltiir Operations (V) / In The Hands Of The Empire (V)
 */
public class Card210_042_BACK extends AbstractObjective {
    public Card210_042_BACK() {
        super(Side.DARK, 7, Title.In_The_Hands_Of_The_Empire);
        setVirtualSuffix(true);
        setGameText("Immediately, may take into hand from Reserve Deck any one card. While this side up, opponent's Force drains are -1 at non-Ralltiir locations. Your total battle destiny is +X, where X = number of Ralltiir locations your Imperials occupy. Always Thinking With Your Stomach is canceled. Flip this card and place a card from hand on Used Pile (if possible) if opponent controls at least two Ralltiir locations.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // This is technically text for the front side, but it seemed needlessly complicated
        //   to attempt to code it as a AddUntilEndOfGameModifierEffect. If there ever is a
        //   "place out of play" condition for this objective, this will need to change.
        GameTextActionId gameTextActionId = GameTextActionId.RALLTIIR_OPERATIONS__LOST_PILE_SWAP;
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getDarkPlayer())
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            action.setText("Exchange a card in hand with a card in Lost Pile");
            action.appendUsage(new OncePerBattleEffect(action));
            return exchangeCardInHandWIthCardOfSameTypeInLostPile(playerId, action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.not(Filters.Ralltiir_location), -1, opponent));
        modifiers.add(new TotalBattleDestinyModifier(self, new OccupiesWithEvaluator(self, playerId, Filters.Ralltiir_location, Filters.Imperial), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Always_Thinking_With_Your_Stomach)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            return immediatelyTakeIntoHandAnyCardFromReserveDeck(game, self, gameTextSourceCardId);
        }
        else if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, opponent, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Ralltiir_location)) {
            RequiredGameTextTriggerAction action = flipBackToFrontSide(game, self, gameTextSourceCardId);
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<RequiredGameTextTriggerAction> immediatelyTakeIntoHandAnyCardFromReserveDeck(final SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.RALLTIIR_OPERATIONS__UPLOAD_CARD;
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Do you want to take a card into hand from Reserve Deck?") {
                                @Override
                                protected void yes() {
                                    game.getGameState().sendMessage(playerId + " chooses to take a card into hand from Reserve Deck");
                                    action.appendEffect(
                                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
                                }
                                @Override
                                protected void no() {
                                    game.getGameState().sendMessage(playerId + " chooses to not take a card into hand from Reserve Deck");
                                }
                            }
                    )
            );
        }
        return Collections.singletonList(action);
    }

    private RequiredGameTextTriggerAction flipBackToFrontSide(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Flip");
        action.setActionMsg(null);
        // Perform result(s)
        action.appendEffect(
                new FlipCardEffect(action, self));
        // Dark side player places a card in used if possible
        if (GameConditions.hasHand(game, playerId))
            action.appendEffect(
                    new PutCardFromHandOnUsedPileEffect(action, playerId, Filters.any, true)
            );
        return action;
    }
}
