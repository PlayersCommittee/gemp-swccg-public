package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCapturedStarshipEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCapturedStarshipEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rules for releasing captured starships with no tractor beams to hold them
 */
public class ReleaseCapturedStarshipsIfNoRelatedTractorBeamsRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates the game rules for releasing captured starships with no tractor beams to hold them.
     * @param actionsEnvironment the actions environment
     */
    public ReleaseCapturedStarshipsIfNoRelatedTractorBeamsRule(ActionsEnvironment actionsEnvironment) {
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

                            String darkSidePlayerId = game.getDarkPlayer();

                            // Check all the cards in play to see if there are any captured starships with no tractor beam at the site or related starship

                            Collection<PhysicalCard> starshipsToRelease = Filters.filterAllOnTable(game,
                                    Filters.and(Filters.opponents(darkSidePlayerId), Filters.captured_starship,
                                            Filters.not(Filters.or(Filters.attachedTo(Filters.hasAttached(Filters.tractor_beam)),
                                                    Filters.attachedTo(Filters.relatedSiteTo(null, Filters.hasAttached(Filters.tractor_beam)))
                                                    )
                                            )
                                    )
                            );

                            if (!starshipsToRelease.isEmpty()) {
                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText("Release captured starship with no tractor beams to hold it");
                                action.setMessage("Release captured starship with no tractor beams to hold it");
                                action.appendEffect(new ReleaseCapturedStarshipEffect(action, starshipsToRelease));

                                triggerActions.add(action);
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                }
        );
    }
}