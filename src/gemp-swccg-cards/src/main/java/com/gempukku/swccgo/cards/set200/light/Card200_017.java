package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardTypeAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: Leesub Sirln (V)
 */
public class Card200_017 extends AbstractAlien {
    public Card200_017() {
        super(Side.LIGHT, 4, 3, 2, 4, 3, "Leesub Sirln", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leesub is a Qiraash (near-human). Enslaved as child. Limited precognition. Selected by Imperial High Inquisitor Tremayne as Force adept. Escaped and hiding in Mos Eisley.");
        setGameText("Once during your control phase, may use 1 Force to name a card type. Reveal top card of opponent's Reserve Deck. If that card matches the named card type, it is lost. Otherwise, replace it or move it to opponent's Used Pile. Immune to attrition < 3.");
        addIcons(Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.QIRAASH);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Name a card type");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new CardTypeAwaitingDecision(game, "Name a card type") {
                                @Override
                                protected void cardTypeChosen(final CardType cardType) {
                                    final GameState gameState = game.getGameState();
                                    gameState.sendMessage(playerId + " guesses " + cardType.getHumanReadable() + " card type");
                                    action.appendEffect(
                                            new RevealTopCardOfReserveDeckEffect(action, playerId, opponent) {
                                                @Override
                                                protected void cardRevealed(final PhysicalCard revealedCard) {
                                                    if (game.getModifiersQuerying().getCardTypes(gameState, revealedCard).contains(cardType)) {
                                                        action.appendEffect(
                                                                new LoseCardFromTopOfReserveDeckEffect(action, opponent, revealedCard));
                                                    }
                                                    else {
                                                        action.appendEffect(
                                                                new PlayoutDecisionEffect(action, playerId,
                                                                        new YesNoDecision("Do you want to place " + GameUtils.getCardLink(revealedCard) + " on opponent's Used Pile?") {
                                                                            @Override
                                                                            protected void yes() {
                                                                                gameState.sendMessage(playerId + " chooses to place " + GameUtils.getCardLink(revealedCard) + " on opponent's Used Pile");
                                                                                action.appendEffect(
                                                                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, revealedCard, Zone.USED_PILE, false));
                                                                            }
                                                                            @Override
                                                                            protected void no() {
                                                                                gameState.sendMessage(playerId + " chooses to not place " + GameUtils.getCardLink(revealedCard) + " on opponent's Used Pile");
                                                                            }
                                                                        }
                                                                )
                                                        );
                                                    }
                                                }
                                            });
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
