package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Walker Sighting
 */
public class Card3_051 extends AbstractLostInterrupt {
    public Card3_051() {
        super(Side.LIGHT, 3, "Walker Sighting", Uniqueness.UNIQUE);
        setLore("'Echo station, 3TA. We have spotted Imperial walkers.' A Rebel tactic is to put as much ground as possible between walkers and Rebel troops, allowing time to prepare a defense.");
        setGameText("If opponent just moved or deployed an AT-AT to a planet site, search your Reserve Deck for up to three exterior sites of that planet and immediately deploy them.  Shuffle, cut and replace.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.WALKER_SIGHTING__DOWNLOAD_SITES;

        // Check condition(s)
        final String systemName;
        if (TriggerConditions.movedToLocationBy(game, effectResult, opponent, Filters.AT_AT, Filters.planet_site)) {
            systemName = ((MovedResult) effectResult).getMovedTo().getPartOfSystem();
        }
        else if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.AT_AT, Filters.planet_site)) {
            systemName = ((PlayCardResult) effectResult).getToLocation().getPartOfSystem();
        }
        else {
            systemName = null;
        }

        if (systemName != null
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy exterior sites from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to three exterior " + systemName + " sites from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.exterior_site, Filters.partOfSystem(systemName)), 1, 3, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}