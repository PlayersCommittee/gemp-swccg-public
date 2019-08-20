package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Quietly Observing (V)
 */
public class Card211_010 extends AbstractNormalEffect {
    public Card211_010() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Quietly Observing", Uniqueness.UNIQUE);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Deploy on table. Aurra, Bossk, and Cad are destiny +2. Once per game, may reveal a unique (•) alien from hand or Reserve Deck (reshuffle); for remainder of game, that card is an assassin and ignores [Reflections II] and [Theed Palace] objective deployment restrictions. [Immune to Alter.]");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        modifiers.add(new DestinyModifier(self, Filters.or(Filters.Aurra, Filters.Bossk, Filters.Cad), 2));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();

        GameTextActionId gameTextActionId = GameTextActionId.QUIETLY_OBSERVING_REVEAL;
        Filter uniqueAliens = Filters.and(Filters.unique, Filters.alien);


        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            if (!game.getGameState().getReserveDeck(playerId).isEmpty()) {
                final TopLevelGameTextAction revealFromReserve = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                revealFromReserve.setText("Reveal a unique (•) alien from Reserve Deck");
                revealFromReserve.setActionMsg("Reveal a unique (•) alien from Reserve Deck");
                revealFromReserve.appendUsage(
                        new OncePerGameEffect(revealFromReserve));
                revealFromReserve.appendEffect(
                        new ChooseCardFromReserveDeckEffect(revealFromReserve, playerId, uniqueAliens) {
                            @Override
                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                setModifiers(self, game, selectedCard);
                            }
                        }
                );
                revealFromReserve.appendEffect(
                        new ShuffleReserveDeckEffect(revealFromReserve));
                actions.add(revealFromReserve);
            }
        }

        Collection<PhysicalCard> uniqueAliensInHand = Filters.filter(game.getGameState().getHand(playerId), game, uniqueAliens);

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && !uniqueAliensInHand.isEmpty()) {
            final TopLevelGameTextAction revealFromHand = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            revealFromHand.setText("Reveal a unique (•) alien from hand");
            revealFromHand.setActionMsg("Reveal a unique (•) alien from hand");
            revealFromHand.appendUsage(
                    new OncePerGameEffect(revealFromHand));
            revealFromHand.appendEffect(
                    new ChooseCardFromHandEffect(revealFromHand, playerId, uniqueAliens) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            setModifiers(self, game, selectedCard);
                        }
                    }
            );
            actions.add(revealFromHand);
        }

        return actions;
    }

    private void setModifiers(PhysicalCard self, SwccgGame game, PhysicalCard selectedCard) {
        GameState gameState = game.getGameState();
        gameState.showCardOnScreen(selectedCard);
        Filter filter = Filters.or(Filters.sameTitleAs(selectedCard, true));
        game.getModifiersEnvironment().addUntilEndOfGameModifier(new KeywordModifier(self, filter, Keyword.ASSASSIN));
        game.getModifiersEnvironment().addUntilEndOfGameModifier(new KeywordModifier(self, filter, Keyword.QUIETLY_OBSERVING));
    }
}