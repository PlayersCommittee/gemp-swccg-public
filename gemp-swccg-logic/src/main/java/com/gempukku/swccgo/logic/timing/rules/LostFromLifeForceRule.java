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
 * Enforces the game rule that allows game text from a card that is lost from life force to perform an action in response to itself
 */
public class LostFromLifeForceRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that allows game text from a card lost from life force to perform an action in response to itself just left table.
     * @param actionsEnvironment the actions environment
     */
    public LostFromLifeForceRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        PhysicalCard cardThatLeftTable = getCardLostFromLifeForce(effectResult);

                        // If game text of card was not previously canceled, get required triggers from card itself
                        if (cardThatLeftTable != null && !cardThatLeftTable.wasPreviouslyCanceledGameText()) {
//                            return cardThatLeftTable.getBlueprint().getLostFromLifeForceRequiredTriggers(game, effectResult, cardThatLeftTable);
                        }

                        return null;
                    }

                    @Override
                    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
                        PhysicalCard cardLostFromLifeForce = getCardLostFromLifeForce(effectResult);
                        List<TriggerAction> allTriggers = new LinkedList<>();
                        // If game text of card was not previously canceled and the card is in the lost pile, get card owner's optional triggers from card itself
                        if (cardLostFromLifeForce != null && !cardLostFromLifeForce.wasPreviouslyCanceledGameText()
                                && cardLostFromLifeForce.getOwner().equals(playerId)
                                && (cardLostFromLifeForce.getZone() == Zone.LOST_PILE
                                    || cardLostFromLifeForce.getZone() == Zone.TOP_OF_LOST_PILE)) {
                            List<TriggerAction> triggers = cardLostFromLifeForce.getBlueprint().getLostFromLifeForceOptionalTriggers(playerId, game, effectResult, cardLostFromLifeForce);
                            if (triggers != null) {
                                // If card is in hand, stacked face up, or top of a card pile and card pile is face up,
                                // then do not show as "off table card action" since card is visible.
                                Zone zone = cardLostFromLifeForce.getZone();
                                if (zone != Zone.HAND && zone != Zone.STACKED && zone != Zone.TOP_OF_LOST_PILE
                                        && (!zone.isCardPile() || zone != GameUtils.getZoneTopFromZone(zone) || !game.getGameState().isCardPileFaceUp(playerId, zone))) {
                                    for (TriggerAction trigger : triggers) {
                                        trigger.setOptionalOffTableCardAction(true);
                                    }
                                }
                                allTriggers.addAll(triggers);
                            }
                        }

                        if (cardLostFromLifeForce != null && !cardLostFromLifeForce.wasPreviouslyCanceledGameText()
                                && cardLostFromLifeForce.getOwner().equals(game.getOpponent(playerId))
                                && (cardLostFromLifeForce.getZone() == Zone.LOST_PILE
                                    || cardLostFromLifeForce.getZone() == Zone.TOP_OF_LOST_PILE)) {
                            List<TriggerAction> triggers = cardLostFromLifeForce.getBlueprint().getOpponentsCardLostFromLifeForceOptionalTriggers(playerId, game, effectResult, cardLostFromLifeForce);
                            if (triggers != null) {
                                // If card is in hand, stacked face up, or top of a card pile and card pile is face up,
                                // then do not show as "off table card action" since card is visible.
                                Zone zone = cardLostFromLifeForce.getZone();
                                if (zone != Zone.HAND && zone != Zone.STACKED && zone != Zone.TOP_OF_LOST_PILE
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
     * Gets the card that was lost
     * @param effectResult the effect result
     * @return the card, or null if not a lost force effect result
     */
    private PhysicalCard getCardLostFromLifeForce(EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST)
            return ((LostForceResult)effectResult).getCardLost();
        return null;
    }
}
