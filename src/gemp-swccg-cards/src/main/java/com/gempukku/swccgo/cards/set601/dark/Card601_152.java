package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Prophetess (V)
 */
public class Card601_152 extends AbstractAlien {
    public Card601_152() {
        super(Side.DARK, 2, 3, 1, 4, 2, "Prophetess", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Renowned female psychic. Predictor of doom. Agent for Governor Aryon of Tatooine. Tailed Jabba and his thugs to Docking Bay 94 when they confronted Han Solo.");
        setGameText("Deploy -1 and forfeit +3 at a site. Once per turn, may peek at top card of opponent's Reserve Deck or Used Pile; may then shuffle that Reserve Deck or Used Pile. Immune to attrition < 3.");
        addKeywords(Keyword.FEMALE);
        addIcons(Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new AtCondition(self, Filters.site), 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Once per turn, may peek at top card of opponent's Reserve Deck or Used Pile; may then shuffle that Reserve Deck or Used Pile.

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && (GameConditions.hasReserveDeck(game, opponent)
                || GameConditions.hasUsedPile(game, opponent))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of a pile");
            action.setActionMsg("Peek at top card of opponent's Reserve Deck or Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(new ChooseExistingCardPileEffect(action, playerId, opponent, Filters.or(Zone.RESERVE_DECK, Zone.USED_PILE)) {
                @Override
                protected void pileChosen(final SwccgGame game, String cardPileOwner, final Zone cardPile) {
                    action.appendEffect(new PeekAtTopCardOfCardPileEffect(action, playerId, opponent, cardPile) {
                        @Override
                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Do you want to shuffle " + opponent + "'s " + cardPile.getHumanReadable() + "?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendEffect(
                                                            new ShufflePileEffect(action, opponent, cardPile));
                                                }
                                                protected void no() {
                                                    game.getGameState().sendMessage(playerId + " chooses to not shuffle " + opponent + "'s " + cardPile.getHumanReadable());
                                                }
                                            }
                                    ));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
