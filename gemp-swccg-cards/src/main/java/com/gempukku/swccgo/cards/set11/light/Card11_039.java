package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;
import com.gempukku.swccgo.logic.timing.results.RaceDestinyStackedResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost Or Starting
 * Title: Podrace Prep
 */
public class Card11_039 extends AbstractLostOrStartingInterrupt {
    public Card11_039() {
        super(Side.LIGHT, 3, "Podrace Prep", Uniqueness.UNIQUE);
        setLore("Advanced preparation in Podracing is usually the key to winning. A little extra work at the start can mean a lot in the long run.");
        setGameText("LOST: Instead of drawing race destiny, use a card from hand. STARTING: Deploy Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace and any Effect that deploys for free. Place Interrupt in Reserve Deck.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isAboutToDrawRaceDestiny(game, effectResult, playerId)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))) {
            final AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;

            // treat it differently if replacing a single race destiny
            if (!aboutToDrawDestinyCardResult.isDrawAndChoose()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Use card from hand for race destiny");
                // Allow response(s)
                action.allowResponses("Use a card from hand for race destiny",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ChooseCardFromHandEffect(action, playerId, Filters.not(self)) {
                                            @Override
                                            protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                                                action.appendEffect(
                                                        new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(selectedCard)) {
                                                            @Override
                                                            protected void refreshedPrintedDestinyValues() {
                                                                final GameState gameState = game.getGameState();
                                                                final PhysicalCard stackRaceDestinyOn = aboutToDrawDestinyCardResult.getStackRaceDestinyOn();
                                                                final float destinyValue = game.getModifiersQuerying().getDestiny(gameState, selectedCard);
                                                                action.appendEffect(
                                                                        new SubstituteDestinyEffect(action, destinyValue));
                                                                action.appendEffect(
                                                                        new PassthruEffect(action) {
                                                                            @Override
                                                                            protected void doPlayEffect(SwccgGame game) {
                                                                                float destiny = selectedCard.getDestinyValueToUse();
                                                                                gameState.removeCardFromZone(selectedCard);
                                                                                selectedCard.setDestinyValueToUse(destiny);
                                                                                selectedCard.setRaceDestinyForPlayer(playerId);
                                                                                gameState.stackCard(selectedCard, stackRaceDestinyOn, false, false, false);
                                                                                game.getActionsEnvironment().emitEffectResult(
                                                                                        new RaceDestinyStackedResult(action, selectedCard, stackRaceDestinyOn));
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            } else {
                // if draw X and choose Y
                /* https://forum.starwarsccg.org/viewtopic.php?p=1334789#p1334789
                    As you are drawing the 3 destinies, you can interrupt any 1 destiny you are about to draw with Podrace Prep and place a card from hand down instead. The card from hand becomes an unresolved destiny draw (not a substituted destiny). It cannot be targeted as "just drawn", since you never drew it.
                    LS then chooses any 2 of the 3 destinies. The destiny not chosen goes to Used pile, even if it's the card that came from your hand.
                 */
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Use card from hand for race destiny");
                // Allow response(s)
                action.allowResponses("Use a card from hand for race destiny",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ChooseCardFromHandEffect(action, playerId, Filters.not(self)) {
                                            @Override
                                            protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                                                //TODO fix this
                                                // for now just put the card from hand on reserve so you draw it next
                                                // this is reasonably close to the same functionality and doesn't crash the game
                                                action.appendEffect(
                                                        new PutCardFromHandOnReserveDeckEffect(action, playerId, selectedCard, false));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
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
        action.allowResponses("deploy Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace, and any Effect that deploys for free",
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