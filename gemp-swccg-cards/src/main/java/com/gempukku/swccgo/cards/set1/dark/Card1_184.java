package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChoosePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Labria
 */
public class Card1_184 extends AbstractAlien {
    public Card1_184() {
        super(Side.DARK, 3, 2, 1, 1, 3, "Labria", Uniqueness.UNIQUE);
        setLore("Information broker. Spy. Devaronian males instinctively have 'wanderlust.' Frustrated that he must be reclusive due to shady past. Suffers from prejudice due to devilish appearance.");
        setGameText("Once each turn, during your control phase, you may reveal the top card of your Reserve Deck to both players. If it is a vehicle or starship, card is immediately lost. Otherwise, return it to the top of your Reserve Deck, Force Pile or Used Pile.");
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.SPY);
        setSpecies(Species.DEVARONIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Reveal top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            if (Filters.or(Filters.vehicle, Filters.starship).accepts(game, revealedCard)) {
                                action.appendEffect(
                                        new LoseCardFromTopOfReserveDeckEffect(action, playerId, revealedCard));
                            }
                            else {
                                action.appendEffect(
                                        new ChoosePileEffect(action, playerId, "Choose card pile to put revealed card on top of",
                                                playerId, new Zone[]{Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE}) {
                                            @Override
                                            public void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                                                action.appendEffect(
                                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, revealedCard, cardPile, false));
                                            }
                                        }
                                );
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
