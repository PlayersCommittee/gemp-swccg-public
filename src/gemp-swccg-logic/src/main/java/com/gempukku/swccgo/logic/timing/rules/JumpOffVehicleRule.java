package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.OptionalRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the game rule that allows characters aboard creature vehicles (or vehicles that allow it) to "jump off" the
 * vehicle when it is about to be lost.
 */
public class JumpOffVehicleRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that allows characters aboard creature vehicles (or vehicles that allow it) to "jump off" the
     * vehicle when it is about to be lost.
     * @param actionsEnvironment the actions environment
     */
    public JumpOffVehicleRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        PhysicalCard cardAboutToBeLost = null;
                        Collection<PhysicalCard> cardsToBeLostInAllCardsSituation = Collections.emptyList();

                        // Check conditions
                        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE) {
                            AboutToLoseCardFromTableResult aboutToLoseCardFromTableResult = (AboutToLoseCardFromTableResult) effectResult;
                            PhysicalCard card = aboutToLoseCardFromTableResult.getCardToBeLost();
                            if (card.getZone().isInPlay()
                                    && (aboutToLoseCardFromTableResult.getPreventableCardEffect() == null
                                    || !aboutToLoseCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card))) {
                                cardAboutToBeLost = card;
                                if (aboutToLoseCardFromTableResult.isAllCardsSituation()) {
                                    cardsToBeLostInAllCardsSituation = aboutToLoseCardFromTableResult.getAllCardsAboutToBeLost();
                                }
                            }
                        }
                        else if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE) {
                            AboutToForfeitCardFromTableResult aboutToForfeitCardToLostPileFromTableResult = (AboutToForfeitCardFromTableResult) effectResult;
                            PhysicalCard card = aboutToForfeitCardToLostPileFromTableResult.getCardToBeForfeited();
                            if (card.getZone().isInPlay()) {
                                cardAboutToBeLost = card;
                            }
                        }

                        if (cardAboutToBeLost == null || !cardAboutToBeLost.getOwner().equals(playerId)) {
                            return null;
                        }

                        // Check that card about to be lost is a creature vehicle (or a vehicle that allows characters to "jump off")
                        if (!Filters.creature_vehicle.accepts(gameState, modifiersQuerying, cardAboutToBeLost)
                                && !modifiersQuerying.allowsCharactersAboardToJumpOff(gameState, cardAboutToBeLost)) {
                            return null;
                        }

                        // Determine characters that may "jump off"
                        List<TriggerAction> actions = new ArrayList<TriggerAction>();

                        Collection<PhysicalCard> charactersAboard = Filters.filter(gameState.getAboardCards(cardAboutToBeLost, false), game,
                                Filters.and(Filters.your(playerId), Filters.character, Filters.not(Filters.in(cardsToBeLostInAllCardsSituation))));
                        for (PhysicalCard characterAboard : charactersAboard) {
                            Action disembarkAction = characterAboard.getBlueprint().getDisembarkAction(characterAboard.getOwner(), game, characterAboard, false, true, Filters.any);
                            if (disembarkAction != null) {

                                // Create action for character to "jump off"
                                OptionalRuleTriggerAction action = new OptionalRuleTriggerAction(_that, characterAboard);
                                action.setText("'Jump off'");
                                action.skipInitialMessageAndAnimation();
                                action.appendEffect(
                                        new StackActionEffect(action, disembarkAction));
                                actions.add(action);
                            }
                        }

                        return actions;
                    }
                }
        );
    }
}
