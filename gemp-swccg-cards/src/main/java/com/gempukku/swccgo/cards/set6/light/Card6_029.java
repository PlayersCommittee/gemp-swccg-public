package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Oola
 */
public class Card6_029 extends AbstractAlien {
    public Card6_029() {
        super(Side.LIGHT, 3, 2, 1, 1, 3, "Oola", Uniqueness.UNIQUE);
        setLore("Female Twi'lek musician. Became a dancer to live a life of luxury. Has worked for Jabba for only two days. Desperate to escape.");
        setGameText("During your control phase, may cause opponent to reveal entire hand by using X Force, where X = number of cards in opponent's hand. All unique (•) male Imperials or unique (•) male aliens there are placed in opponent's Used Pile.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.TWILEK);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            int numForceToUse = GameConditions.numCardsInHand(game, opponent);
            if (numForceToUse > 0
                    && GameConditions.canUseForce(game, playerId, numForceToUse)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Reveal opponent's hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, numForceToUse));
                // Perform result(s)
                action.appendEffect(
                        new RevealOpponentsHandEffect(action, playerId) {
                            @Override
                            protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                action.appendEffect(
                                        new PutCardsFromHandOnUsedPileEffect(action, opponent, Filters.and(Filters.unique, Filters.male, Filters.or(Filters.Imperial, Filters.alien)), false));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
