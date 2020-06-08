package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealUsedPileEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.CardTitleAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.UsedOrLostDecision;
import com.gempukku.swccgo.logic.effects.LoseCardsFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Visored Vision
 */
public class Card4_068 extends AbstractUsedOrLostInterrupt {
    public Card4_068() {
        super(Side.LIGHT, 3, Title.Visored_Vision, Uniqueness.RESTRICTED_2);
        setLore("It's 15 parsecs to Kessel, we've replaced the negative power coupling, it's dark and we're wearing welding goggles. Hit it.");
        setGameText("Name an Interrupt card. Opponent must reveal entire Used Pile, without shuffling. Then you choose: USED: Opponent must lose 1 Force for each copy of that Interrupt found there. LOST: Each copy of that Interrupt found there is lost.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasUsedPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reveal opponent's Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new PlayoutDecisionEffect(action, playerId,
                            new CardTitleAwaitingDecision(game, "Choose an Interrupt card title", CardCategory.INTERRUPT) {
                                @Override
                                protected void cardTitleChosen(final String cardTitle) {
                                    // Allow response(s)
                                    action.allowResponses("Reveal opponent's Used Pile while naming " + cardTitle + " as Interrupt card title",
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RevealUsedPileEffect(action, opponent) {
                                                                @Override
                                                                protected void cardsRevealed(final List<PhysicalCard> revealedCards) {
                                                                    action.appendEffect(
                                                                            new PlayoutDecisionEffect(action, playerId,
                                                                                    new UsedOrLostDecision("Choose USED or LOST for this Interrupt") {
                                                                                        @Override
                                                                                        protected void typeChosen(CardSubtype subtype) {
                                                                                            GameState gameState = game.getGameState();
                                                                                            action.setPlayedAsSubtype(subtype);
                                                                                            gameState.sendMessage(playerId + " chooses to play " + GameUtils.getCardLink(self) + " as " + subtype.getHumanReadable() + " Interrupt");
                                                                                            Collection<PhysicalCard> matchingCards = Filters.filter(revealedCards, game, Filters.title(cardTitle));
                                                                                            if (!matchingCards.isEmpty()) {
                                                                                                gameState.sendMessage("Found " + matchingCards.size() + " Interrupt" + GameUtils.s(matchingCards) + " with card title of " + cardTitle);
                                                                                                if (subtype == CardSubtype.USED) {
                                                                                                    action.appendEffect(
                                                                                                            new LoseForceEffect(action, opponent, matchingCards.size()));
                                                                                                }
                                                                                                else {
                                                                                                    action.appendEffect(
                                                                                                            new LoseCardsFromUsedPileEffect(action, opponent, Filters.in(matchingCards)));
                                                                                                }
                                                                                            }
                                                                                            else {
                                                                                                gameState.sendMessage("No Interrupt with card title of " + cardTitle + " found");
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            )
                                                                    );
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}