package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlaceHandInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Tendau Bendon
 */
public class Card12_032 extends AbstractRepublic {
    public Card12_032() {
        super(Side.LIGHT, 2, 2, 3, 2, 4, "Tendau Bendon", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setPolitics(2);
        setLore("Ithorian senator. Called on by his people to represent them in the Senate, though his heart is not in politics. Voted against the taxation of trade routes.");
        setGameText("Agendas: justice, peace. While in a senate majority, if you have at least one card in hand, once during your control phase may place your hand in Used Pile to draw up to four cards from Reserve Deck.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
        setSpecies(Species.ITHORIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE, Agenda.PEACE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isInSenateMajority(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place hand in Used Pile to draw cards");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceHandInUsedPileEffect(action, playerId));
            action.appendCost(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            int maxCardsToDraw = Math.min(4, game.getGameState().getReserveDeckSize(playerId));
                            if (maxCardsToDraw > 0) {
                                action.appendCost(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose number of cards to draw ", 1, maxCardsToDraw, maxCardsToDraw) {
                                                    @Override
                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                        action.setActionMsg("Draw " + result + " cards from Reserve Deck");
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                                    }
                                                }
                                        ));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
