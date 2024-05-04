package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that allows game text from a card that leaves table to perform an action in response to itself
 * just left table.
 */
public class LeavesTableCardRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that allows game text from a just lost card to perform an action in response to itself just left table.
     * @param actionsEnvironment the actions environment
     */
    public LeavesTableCardRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        PhysicalCard cardThatLeftTable = getCardThatLeftTable(effectResult);

                        // If game text of card was not previously canceled, get required triggers from card itself
                        if (cardThatLeftTable != null && !cardThatLeftTable.wasPreviouslyCanceledGameText()) {
                            return cardThatLeftTable.getBlueprint().getLeavesTableRequiredTriggers(game, effectResult, cardThatLeftTable);
                        }

                        return null;
                    }

                    @Override
                    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
                        PhysicalCard cardThatLeftTable = getCardThatLeftTable(effectResult);
                        List<TriggerAction> allTriggers = new LinkedList<>();
                        // If game text of card was not previously canceled, get card owner's optional triggers from card itself
                        if (cardThatLeftTable != null && !cardThatLeftTable.wasPreviouslyCanceledGameText()
                                && cardThatLeftTable.getOwner().equals(playerId)) {
                            List<TriggerAction> triggers = cardThatLeftTable.getBlueprint().getLeavesTableOptionalTriggers(playerId, game, effectResult, cardThatLeftTable);
                            if (triggers != null) {
                                // If card is in hand, stacked face up, or top of a card pile and card pile is face up,
                                // then do not show as "off table card action" since card is visible.
                                Zone zone = cardThatLeftTable.getZone();
                                if (zone != Zone.HAND && zone != Zone.STACKED
                                        && (!zone.isCardPile() || zone != GameUtils.getZoneTopFromZone(zone) || !game.getGameState().isCardPileFaceUp(playerId, zone))) {
                                    for (TriggerAction trigger : triggers) {
                                        trigger.setOptionalOffTableCardAction(true);
                                    }
                                }
                                allTriggers.addAll(triggers);
                            }
                        }

                        if (cardThatLeftTable != null && !cardThatLeftTable.wasPreviouslyCanceledGameText()
                                && cardThatLeftTable.getOwner().equals(game.getOpponent(playerId))) {
                            List<TriggerAction> triggers = cardThatLeftTable.getBlueprint().getOpponentsCardLeavesTableOptionalTriggers(playerId, game, effectResult, cardThatLeftTable);
                            if (triggers != null) {
                                // If card is in hand, stacked face up, or top of a card pile and card pile is face up,
                                // then do not show as "off table card action" since card is visible.
                                Zone zone = cardThatLeftTable.getZone();
                                if (zone != Zone.HAND && zone != Zone.STACKED
                                        && (!zone.isCardPile() || zone != GameUtils.getZoneTopFromZone(zone) || !game.getGameState().isCardPileFaceUp(playerId, zone))) {
                                    for (TriggerAction trigger : triggers) {
                                        trigger.setOptionalOffTableCardAction(true);
                                    }
                                }
                                allTriggers.addAll(triggers);
                            }
                        }

                        return allTriggers;
                    }
                });
    }

    /**
     * Gets the card that left the table.
     * @param effectResult the effect result
     * @return the card, or null if not a leaves table effect result
     */
    private PhysicalCard getCardThatLeftTable(EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)
            return ((LostFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)
            return ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE)
            return ((CancelCardOnTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE)
            return ((StackedFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_TABLE)
            return ((ReturnedCardToHandFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_RESERVE_DECK_FROM_TABLE)
            return ((PutCardInReserveDeckFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_FORCE_PILE_FROM_TABLE)
            return ((PutCardInForcePileFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE)
            return ((PutCardInUsedPileFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE)
            return ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard();
        else if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult captureCharacterResult = (CaptureCharacterResult) effectResult;
             if (captureCharacterResult.getOption()== CaptureOption.ESCAPE) {
                 return captureCharacterResult.getCapturedCard();
             }
        }
        return null;
    }
}
