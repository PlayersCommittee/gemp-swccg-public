package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jabba's Twerps
 */
public class Card7_256 extends AbstractLostInterrupt {
    public Card7_256() {
        super(Side.DARK, 6, "Jabba's Twerps", Uniqueness.UNIQUE);
        setLore("'Look, Jabba, next time you want to see me, come see me yourself. Don't send one of these twerps.'");
        setGameText("If opponent just initiated battle where your alien leader is present, deploy up to three non-unique aliens to that location from Reserve Deck; reshuffle. OR Once per game, retrieve 1 Force for each of your alien leaders present at any battleground sites.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_TWERPS__DOWNLOAD_NON_UNIQUE_ALIENS;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.alien_leader)))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy non-unique aliens from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to three non-unique aliens from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsToLocationFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.alien), 1, 3, Filters.battleLocation, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_TWERPS__RETRIEVE_FORCE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final int amountToRetrieve = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.alien_leader,
                    Filters.presentAt(Filters.battleground_site), Filters.mayContributeToForceRetrieval));
            if (amountToRetrieve > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Retrieve " + amountToRetrieve + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, amountToRetrieve));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}