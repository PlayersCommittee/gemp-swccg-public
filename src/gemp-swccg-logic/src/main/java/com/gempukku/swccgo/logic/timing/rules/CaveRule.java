package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ChangeAsteroidCaveOrSpaceSlugBellyResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the Cave Rules.
 */
public class CaveRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces Cave Rules.
     * @param actionsEnvironment the actions environment
     */
    public CaveRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            GameState gameState = game.getGameState();

                            // Check all Big One: Asteroid Cave or Space Slug Belly in play to see if any need to change
                            for (PhysicalCard card : Filters.filterTopLocationsOnTable(game, Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly)) {

                                // Check if there is a Space Slug at the related Big One
                                boolean spaceSlugExists = Filters.canSpotFromAllOnTable(game, Filters.and(Filters.Space_Slug, Filters.at(Filters.relatedBigOne(card))));
                                if (spaceSlugExists != card.isSpaceSlugBelly()) {
                                    // Change from Big One: Asteroid Cave to Space Slug Belly (or vice versa)
                                    triggerActions.add(getChangeSiteAction(gameState, card, true));
                                }
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                });
    }

    /**
     * Creates an action to change Big One: Asteroid Cave to Space Slug Belly (or vice versa).
     * @param gameState the game state
     * @param cardToChange the card to change
     * @param toBelly true if changing to Space Slug Belly, false if changing to Big One: Asteroid Cave
     * @return the action
     */
    private TriggerAction getChangeSiteAction(final GameState gameState, final PhysicalCard cardToChange, final boolean toBelly) {
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, cardToChange);
        action.setSingletonTrigger(true);
        action.skipInitialMessageAndAnimation();
        if (toBelly)
            action.setText("Change to " + Title.Space_Slug_Belly);
        else
            action.setText("Change to " + Title.Big_One_Asteroid_Cave);
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String oldCardLink = GameUtils.getCardLink(cardToChange);
                        cardToChange.setSpaceSlugBelly(toBelly);
                        // Also change any converted locations underneath
                        for (PhysicalCard convertedLocation : gameState.getConvertedLocationsUnderTopLocation(cardToChange)) {
                            convertedLocation.setSpaceSlugBelly(toBelly);
                        }
                        String newCardLink = GameUtils.getCardLink(cardToChange);
                        gameState.sendMessage(oldCardLink + " is changed to " + newCardLink);
                        gameState.cardAffectsCard(null, cardToChange, cardToChange);
                        _actionsEnvironment.emitEffectResult(new ChangeAsteroidCaveOrSpaceSlugBellyResult(null, cardToChange));
                    }
                });
        return action;
    }
}
