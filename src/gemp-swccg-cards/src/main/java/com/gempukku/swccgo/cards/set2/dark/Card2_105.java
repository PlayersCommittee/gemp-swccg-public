package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Tech Mo'r
 */
public class Card2_105 extends AbstractAlien {
    public Card2_105() {
        super(Side.DARK, 6, 3, 1, 1, 1, "Tech Mo'r", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Male Bith musician. Plays Ommni Box in Figrin D'an's band. Lost ownership of instrument to Figrin while gambling.");
        setGameText("After using Ommni Box, peek at top X cards of pile or deck just shuffled, where X = number of other musicians at same site. May reshuffle without peeking.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.BITH);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter musicianFilter = Filters.and(Filters.musician, Filters.atSameSite(self));

        // Check condition(s)
        if (TriggerConditions.cardPileJustShuffledBy(game, effectResult, Filters.and(Filters.your(self), Filters.Ommni_Box))
                && GameConditions.canSpot(game, self, musicianFilter)) {
            ShufflingResult result = (ShufflingResult) effectResult;
            final String zoneOwner = result.getZoneOwner();
            final Zone cardPile = result.getCardPile();
            int numMusicians = Filters.countActive(game, self, musicianFilter);
            int pileSize = game.getGameState().getCardPileSize(zoneOwner, cardPile);
            final int count = Math.min(numMusicians, pileSize);
            if (count > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Peek at top cards of shuffled pile");
                // Perform result(s)
                action.appendEffect(
                        new PeekAtTopCardsOfCardPileEffect(action, playerId, zoneOwner, cardPile, count) {
                            @Override
                            protected void cardsPeekedAt(List<PhysicalCard> cards) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to re-shuffle this card pile?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new ShufflePileEffect(action, zoneOwner, cardPile));
                                                    }
                                                    @Override
                                                    protected void no() {
                                                        game.getGameState().sendMessage(playerId + " chooses to not shuffle " + cardPile.getHumanReadable());
                                                    }
                                                }));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
