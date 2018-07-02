package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LookAtLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Keep Your Eyes Open
 */
public class Card5_056 extends AbstractUsedInterrupt {
    public Card5_056() {
        super(Side.LIGHT, 5, Title.Keep_Your_Eyes_Open);
        setLore("'Look, don't worry. Everything's gonna be fine. Trust me.'");
        setGameText("If a battle was just initiated where you have two smugglers, you may move any or all of your characters, vehicles and starships away (at normal use of the Force). OR Glance at the cards in any Lost Pile and replace unshuffled.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.smuggler, Filters.with(self, Filters.and(Filters.your(self), Filters.smuggler)))))) {
            final Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship), Filters.participatingInBattle);
            if (GameConditions.canSpot(game, self, Filters.and(filter, Filters.movableAsMoveAway(playerId, false, 0, Filters.any)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move characters, vehicles, and starships away");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardsAwayEffect(action, playerId, filter, false));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Look at Lost Pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.LOST_PILE) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            // Allow response(s)
                            action.allowResponses("Look at " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LookAtLostPileEffect(action, playerId, cardPileOwner));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}