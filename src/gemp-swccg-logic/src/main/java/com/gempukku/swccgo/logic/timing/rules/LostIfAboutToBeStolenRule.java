package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the game rule that causes certain cards to be lost anytime they are about to be stolen. (Example: Obi-Wan's Journal)
 */
public class LostIfAboutToBeStolenRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes certain cards to be lost anytime they are about to be stolen. (Example: Obi-Wan's Journal).
     * @param actionsEnvironment the actions environment
     */
    public LostIfAboutToBeStolenRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        // Check conditions
                        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_STOLEN) {
                            final AboutToBeStolenResult aboutToBeStolenResult = (AboutToBeStolenResult) effectResult;
                            final PhysicalCard cardAboutToBeStolen = aboutToBeStolenResult.getCardToBeStolen();
                            if (cardAboutToBeStolen == null || aboutToBeStolenResult.getPreventableCardEffect().isEffectOnCardPrevented(cardAboutToBeStolen)) {
                                return null;
                            }

                            // Check that card is lost anytime it is about to be stolen
                            if (!modifiersQuerying.isLostIfAboutToBeStolen(gameState, cardAboutToBeStolen)) {
                                return null;
                            }

                            List<TriggerAction> actions = new ArrayList<TriggerAction>();

                            // Create action card to be lost
                            RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, cardAboutToBeStolen);
                            action.setText("Make " + GameUtils.getFullName(cardAboutToBeStolen) + " lost");
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            aboutToBeStolenResult.getPreventableCardEffect().preventEffectOnCard(cardAboutToBeStolen);
                                        }
                                    }
                            );
                            action.appendEffect(
                                    new LoseCardsFromOffTableSimultaneouslyEffect(action, Collections.singleton(cardAboutToBeStolen), false));
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, cardAboutToBeStolen));
                            actions.add(action);

                            return actions;
                        }

                        return null;
                    }
                }
        );
    }
}
