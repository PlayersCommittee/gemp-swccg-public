package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: M'iiyoom Onith
 */
public class Card1_187 extends AbstractAlien {
    public Card1_187() {
        super(Side.DARK, 3, 3, 1, 1, 3, Title.Miiyoom_Onith, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Female H'nemthe, a species whose females ritually kill their mates. Stranded on Tatooine due to questionable passage tax. Razor-sharp tongue. M'iiyoom means, 'nightlilly'.");
        setGameText("Once during each of your control phases, may reveal opponent's hand by using X Force, where X = number of cards in opponent's hand. All unique (•) male Rebels and unique (•) male aliens there are lost.");
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.HNEMTHE);
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
                                        new LoseCardsFromHandEffect(action, opponent, Filters.and(Filters.unique, Filters.male, Filters.or(Filters.Rebel, Filters.alien))));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
