package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CollapsedSiteResult;

import java.util.Collection;

/**
 * An effect that 'collapses' the specified site.
 */
public class CollapseSiteEffect extends AbstractSubActionEffect {
    private PhysicalCard _site;

    /**
     * Creates an effect that 'collapses' the specified site.
     * @param action the action performing this effect
     * @param site the site to 'collapse'
     */
    public CollapseSiteEffect(Action action, PhysicalCard site) {
        super(action);
        _site = site;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final String performingPlayerId = _action.getPerformingPlayer();
        final PhysicalCard sourceCard = _action.getActionSource();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Collapse the site
                        gameState.sendMessage(GameUtils.getCardLink(_site) + " is 'collapsed' by " + GameUtils.getCardLink(sourceCard));
                        gameState.cardAffectsCard(performingPlayerId, sourceCard, _site);
                        gameState.collapseSite(_site);

                        // Determine all the cards to be lost
                        Collection<PhysicalCard> cardsToMakeLost = Filters.filterAllOnTable(game,
                                Filters.and(Filters.or(Filters.character, Filters.vehicle, Filters.starship, Filters.weapon, Filters.device), Filters.or(Filters.at(_site), Filters.stackedOn(_site))));
                        if (!cardsToMakeLost.isEmpty()) {
                            // The cards are all lost
                            SubAction loseCardsOnTableAction = new SubAction(subAction);
                            loseCardsOnTableAction.appendEffect(
                                    new LoseCardsFromTableEffect(loseCardsOnTableAction, cardsToMakeLost, true));

                            // Stack sub-action
                            subAction.stackSubAction(loseCardsOnTableAction);
                        }

                        // Emit the effect result after the cards at the site are all lost
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new CollapsedSiteResult(performingPlayerId, sourceCard, _site, cardsToMakeLost)));
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
