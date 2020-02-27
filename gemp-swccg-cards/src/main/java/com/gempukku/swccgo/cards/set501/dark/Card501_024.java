package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Quietly Observing (V) (Errata)
 */
public class Card501_024 extends AbstractNormalEffect {
    public Card501_024() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Quietly Observing", Uniqueness.UNIQUE);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Text: Deploy on table. Aurra, Bossk, and Cad are destiny +2. Once per game, may reveal up to two unique (•) aliens from hand and/or Reserve Deck (reshuffle); for remainder of game, those cards are assassins and Black Sun agents. [Immune to Alter.]");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Quietly Observing (V) (Errata)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DestinyModifier(self, Filters.or(Filters.Aurra, Filters.Bossk, Filters.Cad), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUIETLY_OBSERVING_REVEAL;
        final Filter uniqueAliens = Filters.and(Filters.unique, Filters.alien);
        final Collection<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
        final Collection<PhysicalCard> uniqueAliensInHand = Filters.filter(cardsInHand, game, uniqueAliens);

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal up to two unique (•) aliens");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );

            if (!uniqueAliensInHand.isEmpty()) {
                int numAliensInHand = Math.min(2, uniqueAliensInHand.size());
                String[] possibleResults = new String[numAliensInHand + 1];
                for (int i = 0; i < numAliensInHand + 1; i++) {
                    possibleResults[i] = String.valueOf(i);
                }

                action.appendTargeting(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Choose number of aliens to reveal from hand", possibleResults) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        switch (index) {
                                            case 0:
                                                action.appendTargeting(
                                                        new ChooseCardsFromReserveDeckEffect(action, playerId, 0, 2, uniqueAliens) {
                                                            @Override
                                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                                for (PhysicalCard selectedCard : selectedCards) {
                                                                    setModifiers(self, game, selectedCard, action);
                                                                }
                                                            }
                                                        }
                                                );
                                                action.appendEffect(
                                                        new ShuffleReserveDeckEffect(action)
                                                );
                                                break;
                                            case 1:
                                                action.appendTargeting(
                                                        new ChooseCardFromHandEffect(action, playerId, uniqueAliens) {
                                                            @Override
                                                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                                                setModifiers(self, game, selectedCard, action);
                                                            }
                                                        }
                                                );
                                                action.appendTargeting(
                                                        new ChooseCardsFromReserveDeckEffect(action, playerId, 0, 1, uniqueAliens) {
                                                            @Override
                                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                                for (PhysicalCard selectedCard : selectedCards) {
                                                                    setModifiers(self, game, selectedCard, action);
                                                                }
                                                            }
                                                        }
                                                );
                                                action.appendEffect(
                                                        new ShuffleReserveDeckEffect(action)
                                                );
                                                break;
                                            case 2:
                                                action.appendTargeting(
                                                        new ChooseCardsFromHandEffect(action, playerId, 0, 2, uniqueAliens) {
                                                            @Override
                                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                                for (PhysicalCard selectedCard : selectedCards) {
                                                                    setModifiers(self, game, selectedCard, action);
                                                                }
                                                            }
                                                        }
                                                );
                                                break;
                                        }
                                    }
                                })
                );
            } else {
                action.appendTargeting(
                        new ChooseCardsFromReserveDeckEffect(action, playerId, 0, 2, uniqueAliens) {
                            @Override
                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                for (PhysicalCard selectedCard : selectedCards) {
                                    setModifiers(self, game, selectedCard, action);
                                }
                            }
                        }
                );
                action.appendEffect(
                        new ShuffleReserveDeckEffect(action)
                );
            }
            return Collections.singletonList(action);
        }

        return null;
    }

    private void setModifiers(PhysicalCard self, SwccgGame game, PhysicalCard selectedCard, Action action) {
        GameState gameState = game.getGameState();
        gameState.showCardOnScreen(selectedCard);
        Filter filter = Filters.or(Filters.sameTitleAs(selectedCard, true));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(
                        action, new KeywordModifier(self, filter, Keyword.ASSASSIN), GameUtils.getCardLink(selectedCard) + " is an Assassin for remainder of Game"
                )
        );
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(
                        action, new KeywordModifier(self, filter, Keyword.BLACK_SUN_AGENT), GameUtils.getCardLink(selectedCard) + " is a Black Sun Agent for remainder of Game"
                )
        );
    }
}