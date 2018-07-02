package com.gempukku.swccgo.logic.timing.rules;


import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackRunState;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the game rule that if there is no lead starfighter, one of the wingmen (if Proton Torpedoes aboard) may be
 * selected to become the lead starfighter.
 */
public class AttackRunLeadStarfighterRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that if a character of the same persona of the Jedi Test target was just deployed or placed in play,
     * the Jedi Test is updated to point to that character.
     * @param actionsEnvironment the actions environment
     */
    public AttackRunLeadStarfighterRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult) {
                        final GameState gameState = game.getGameState();
                        EpicEventState epicEventState = gameState.getEpicEventState();

                        // Check condition(s)
                        if (!game.getLightPlayer().equals(playerId)) {
                            return null;
                        }
                        if (epicEventState == null || epicEventState.getEpicEventType() != EpicEventState.Type.ATTACK_RUN) {
                            return null;
                        }
                        final AttackRunState attackRunState = (AttackRunState) epicEventState;
                        if (attackRunState.getLeadStarfighter() == null) {
                            return null;
                        }
                        if (!TriggerConditions.isTableChanged(game, effectResult)) {
                            return null;
                        }
                        if (Filters.canSpot(game, null, Filters.lead_starfighter_in_Attack_Run)) {
                            return null;
                        }
                        // Update state to indicate no lead starfighter
                        attackRunState.setLeadStarfighter(null);

                        // Try to find a new lead starfighter
                        PhysicalCard attackRun = Filters.findFirstActive(game, null, Filters.Attack_Run);
                        if (attackRun == null) {
                            return null;
                        }
                        Filter validLeadStarfighterFilter = Filters.and(Filters.wingmen_in_Attack_Run, Filters.piloted, Filters.hasAttached(Filters.Proton_Torpedoes));
                        Collection<PhysicalCard> validLeadStarfighter = Filters.filterActive(game, null, validLeadStarfighterFilter);
                        if (validLeadStarfighter.isEmpty()) {
                            return null;
                        }

                        // Create action for choosing new
                        OptionalRuleTriggerAction action = new OptionalRuleTriggerAction(_that, attackRun);
                        action.setSingletonTrigger(true);
                        action.setText("Choose new lead starfighter");
                        action.skipInitialMessageAndAnimation();
                        action.appendEffect(
                                new ChooseCardOnTableEffect(action, playerId, "Identify new lead starfighter", validLeadStarfighter) {
                                    @Override
                                    protected void cardSelected(PhysicalCard leadStarfighter) {
                                        attackRunState.setLeadStarfighter(leadStarfighter);
                                        List<PhysicalCard> wingmen = attackRunState.getWingmen();
                                        wingmen.remove(leadStarfighter);
                                        gameState.sendMessage(GameUtils.getCardLink(leadStarfighter) + " is lead starfighter" + (!wingmen.isEmpty() ? (" with wingmen " + GameUtils.getAppendedNames(wingmen)) : ""));
                                    }
                                });
                        return Collections.singletonList((TriggerAction) action);
                    }
                }
        );
    }
}

