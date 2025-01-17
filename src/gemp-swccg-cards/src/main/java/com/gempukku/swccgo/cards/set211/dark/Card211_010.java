package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Quietly Observing (V)
 */
public class Card211_010 extends AbstractNormalEffect {
    public Card211_010() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Quietly_Observing, Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Deploy on table. While alone, assassins are power +1. Once per game, may reveal up to two unique (•) aliens from hand and/or Reserve Deck (reshuffle); for remainder of game, those cards are assassins and, if your [Reflections II] objective on table, Black Sun agents. [Immune to Alter.]");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.alone, Filters.assassin), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUIETLY__OBSERVING_REVEAL;
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

                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Choose number of aliens to reveal from hand", possibleResults) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        switch (index) {
                                            case 0:
                                                chooseCardFromReserveDeck(action, playerId, uniqueAliens, self, 2);
                                                break;
                                            case 1:
                                                action.appendEffect(
                                                        new ChooseCardFromHandEffect(action, playerId, uniqueAliens) {
                                                            @Override
                                                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new YesNoDecision("Reveal another alien from Reserve Deck?") {
                                                                                    @Override
                                                                                    protected void yes() {
                                                                                        chooseCardFromReserveDeck(action, playerId, uniqueAliens, self, 1);
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                                appendEffects(game, self, selectedCard, action, false);
                                                            }
                                                        }
                                                );
                                                break;
                                            case 2:
                                                action.appendEffect(
                                                        new ChooseCardsFromHandEffect(action, playerId, 0, 2, uniqueAliens) {
                                                            @Override
                                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                                for (PhysicalCard selectedCard : selectedCards) {
                                                                    appendEffects(game, self, selectedCard, action, false);
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
                chooseCardFromReserveDeck(action, playerId, uniqueAliens, self, 2);
            }
            return Collections.singletonList(action);
        }

        return null;
    }

    private void chooseCardFromReserveDeck(final TopLevelGameTextAction action, String playerId, Filter uniqueAliens, final PhysicalCard self, int max) {
        action.appendEffect(
                new ChooseCardsFromReserveDeckEffect(action, playerId, 0, max, uniqueAliens) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        for (PhysicalCard selectedCard : selectedCards) {
                            appendEffects(game, self, selectedCard, action, true);
                        }
                    }
                }
        );
        action.appendEffect(
                new ShuffleReserveDeckEffect(action)
        );
    }

    private void appendEffects(SwccgGame game, PhysicalCard self, PhysicalCard selectedCard, TopLevelGameTextAction action, boolean fromReserveDeck) {
        String revealedFromLocation;
        Filter filter = Filters.sameTitleAs(selectedCard, true);
        if(fromReserveDeck){
            revealedFromLocation = "Reserve Deck";
        }else{
            revealedFromLocation = "Hand";
        }
        action.appendEffect(
                new ShowCardOnScreenEffect(action, selectedCard)
        );
        action.appendEffect(
                new SendMessageEffect(action, self.getOwner() + " reveals " + GameUtils.getCardLink(selectedCard) + " from " + revealedFromLocation)
        );
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(
                        action, new KeywordModifier(self, filter, Keyword.ASSASSIN), GameUtils.getCardLink(selectedCard) + " is an Assassin for remainder of Game "
                )
        );
        if (GameConditions.canTarget(game, self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective))) {
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(
                            action, new KeywordModifier(self, filter, Keyword.BLACK_SUN_AGENT), GameUtils.getCardLink(selectedCard) + " is a Black Sun Agent for remainder of Game"
                    )
            );
        }
    }
}