package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Escape Pod (V)
 */
public class Card601_043 extends AbstractUsedInterrupt {
    public Card601_043() {
        super(Side.LIGHT, 6, Title.Escape_Pod);
        setVirtualSuffix(true);
        setLore("Capital starships have emergency escape pods. Equipped with food, water, flares, medpacs, hunting blaster and tracking beacon (R2-D2 deactivated this one's beacon).");
        setGameText("[Upload] a dejarik, hologram, or holosite. OR Cancel a Force drain at a holosite. OR If opponent is about to place your character out of play, place that character in your Lost Pile instead.");
        addIcons(Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ESCAPE_POD__UPLOAD_DEJARIK_OR_HOLOSITE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take dejarik, hologram, or holosite into hand from Reserve Deck");
            // Pay cost(s)
            // Allow response(s)
            action.allowResponses("Take dejarik, hologram, or holosite into hand from Reserve Deck" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.dejarik, Filters.hologram, Filters.holosite), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.holosite)
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        Filter yourCharacter = Filters.and(Filters.your(self), Filters.character);

        // Check condition(s)
        if (TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, opponent, yourCharacter)) {
            final AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
            final PhysicalCard card = result.getCardToBePlacedOutOfPlay();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place " + GameUtils.getFullName(card) + " in Lost Pile");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(card) + " in Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            result.getPreventableCardEffect().preventEffectOnCard(card);
                            action.appendEffect(
                                    new PlaceCardInLostPileFromTableEffect(action, card));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isAboutToBePlacedOutOfPlayFromOffTable(game, effectResult, opponent, yourCharacter)) {
            final AboutToPlaceCardOutOfPlayFromOffTableResult result = (AboutToPlaceCardOutOfPlayFromOffTableResult) effectResult;
            final PhysicalCard card = result.getCardToBePlacedOutOfPlay();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place " + GameUtils.getFullName(card) + " in Lost Pile");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(card) + " in Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            result.getPreventableCardEffect().preventEffectOnCard(card);
                            action.appendEffect(
                                    new LoseCardsFromOffTableSimultaneouslyEffect(action, Collections.singleton(card), false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}