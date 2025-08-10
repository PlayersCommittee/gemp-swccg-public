package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.RuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * Enforces the game rule that cards not allowed to participate in battle are automatically excluded from battle.
 */
public class ExcludeBattleParticipantsRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that cards not allowed to participate in battle are automatically excluded from battle.
     * @param actionsEnvironment the actions environment
     */
    public ExcludeBattleParticipantsRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        BattleState battleState = gameState.getBattleState();
                        if (battleState == null) {
                            return null;
                        }
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        List<TriggerAction> actions = new ArrayList<TriggerAction>();

                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            // Find any cards that are participating in the battle that are not allowed to be participating in the battle.
                            Collection<PhysicalCard> ineligibleParticipants = Filters.filterActive(game, null,
                                    Filters.and(Filters.in(battleState.getAllCardsParticipating()), Filters.at(battleState.getBattleLocation()), Filters.not(Filters.canParticipateInBattleAt(battleState.getBattleLocation(), battleState.getPlayerInitiatedBattle()))));
                            if (ineligibleParticipants.isEmpty()) {
                                // Since there are no cards to exclude, just make update the participants in case any cards need to join the battle (or are no longer in the battle).
                                battleState.updateParticipants(game);
                                return null;
                            }

                            List<PhysicalCard> cardsExcludedByRule = new ArrayList<PhysicalCard>();
                            Map<PhysicalCard, PhysicalCard> cardsExcludedByCard = new HashMap<PhysicalCard, PhysicalCard>();

                            for (PhysicalCard cardToExclude : ineligibleParticipants) {
                                // Get card that is causing this card to not be able to participate in the battle.
                                PhysicalCard source = modifiersQuerying.getCardCausingExclusionFromBattle(gameState, cardToExclude);
                                if (source == null)
                                    cardsExcludedByRule.add(cardToExclude);
                                else
                                    cardsExcludedByCard.put(cardToExclude, source);
                            }

                            // If any cards are excluded by rule, exclude those cards first.
                            if (!cardsExcludedByRule.isEmpty()) {

                                RuleTriggerAction excludeAction = new RequiredRuleTriggerAction(_that);
                                excludeAction.setText("Exclude cards from battle");
                                excludeAction.appendEffect(
                                        new ExcludeFromBattleEffect(excludeAction, cardsExcludedByRule));
                                actions.add(excludeAction);
                                return actions;
                            }

                            // Otherwise, create actions to exclude cards that are excluded by a card.
                            if (!cardsExcludedByCard.isEmpty()) {
                                RuleTriggerAction excludeAction = new RequiredRuleTriggerAction(_that);
                                excludeAction.setText("Exclude " + GameUtils.getAppendedTextNames(cardsExcludedByCard.keySet()) + " from battle");
                                excludeAction.appendEffect(
                                        new ExcludeFromBattleEffect(excludeAction, cardsExcludedByCard));
                                actions.add(excludeAction);
                            }
                        }

                        return actions;
                    }
                }
        );
    }
}
