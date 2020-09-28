package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfCardPileAndTakeCardsIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Alien
 * Title: Maz Kanata
 */
public class Card211_057 extends AbstractAlien {
    public Card211_057() {
        super(Side.LIGHT, 2, 2, 2, 4, 4, Title.Maz, Uniqueness.UNIQUE);
        setLore("Female Information broker and leader");
        // Text was updated sometime around 7/5/2019
        setGameText("[Pilot] 1. Opponent's total battle destiny is -1 here. During your turn, may reveal the top three cards of your Reserve Deck, take one alien into hand (if possible), and shuffle your Reserve Deck.");
        addPersona(Persona.MAZ);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
        addKeywords(Keyword.FEMALE, Keyword.INFORMATION_BROKER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter here = Filters.here(self);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new TotalBattleDestinyModifier(self, here, -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.MAZ__PEEK_AT_TOP_OF_RESERVE_DECK;
        if (GameConditions.isDuringYourTurn(game, self.getOwner())
                && GameConditions.isOnceDuringYourTurn(game, self, self.getOwner(), gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, self.getOwner()))
        {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal the top three cards of your reserve deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new RevealTopCardsOfCardPileAndTakeCardsIntoHandEffect(action, playerId, playerId, Zone.RESERVE_DECK, Filters.alien, 3));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action));

            return Collections.singletonList(action);
        }
        return null;
    }
}