package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToLoseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost Or Starting
 * Title: Start Your Engines!
 */
public class Card11_087 extends AbstractLostOrStartingInterrupt {
    public Card11_087() {
        super(Side.DARK, 3, "Start Your Engines!", Uniqueness.UNIQUE);
        setLore("Podracing is based on ancient contests that utilized animal-drawn carts. Today, Podracing's reputation is known to be incredibly fast and dangerous.");
        setGameText("LOST: Peek at top 3 cards of your Reserve Deck; place all but one in Lost Pile. STARTING: Deploy Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace, and any Effect that deploys for free. Place Interrupt in Reserve Deck.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Peek at top 3 cards of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Peek at top 3 cards of Reserve Deck and place all but one in Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToLoseEffect(action, playerId, 3, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace, and any Effect that deploys for free",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        if (GameConditions.hasInReserveDeck(game, playerId, Filters.Podrace_Arena)
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.Podracer)
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.Boonta_Eve_Podrace)
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Effect, Filters.deploysForFree))) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Podrace_Arena, true, false));
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Podracer, Filters.Podrace_Arena, true, false));
                            action.appendEffect(
                                    new DeployCardsToTargetFromReserveDeckEffect(action, opponent, Filters.Podracer, 0, 1, Filters.Podrace_Arena, true, false) {
                                        @Override
                                        public String getChoiceText(int numCardsToChoose) {
                                            return "Choose Podracer deploy to Podrace Arena";
                                        }
                                    });
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Boonta_Eve_Podrace, true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree), true, false));
                        }
                        else {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.none, true, false));
                        }
                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}