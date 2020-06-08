package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Major Panno
 */
public class Card9_027 extends AbstractRebel {
    public Card9_027() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Major_Panno, Uniqueness.UNIQUE);
        setLore("Male Dresselian scout. Former commando. Tactician. Works with General Madine to plan logistics of strike operations.");
        setGameText("While at a system or an exterior battleground site, allows you to activate up to 2 additional Force for each related exterior battleground site you occupy with a scout (limit 4 additional Force).");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.DRESSELIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.ACTIVATE)
                && GameConditions.isAtLocation(game, self, Filters.or(Filters.system, Filters.exterior_battleground_site))
                && GameConditions.canActivateForce(game, playerId)) {
            int maxForceToActivate = Math.min(4, 2 * Filters.countTopLocationsOnTable(game, Filters.and(Filters.relatedSite(self), Filters.exterior_battleground_site, Filters.occupiesWith(playerId, self, Filters.scout))));
            if (maxForceToActivate > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Activate Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        game.getGameState().sendMessage(playerId + " chooses to activate " + result + " Force");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
