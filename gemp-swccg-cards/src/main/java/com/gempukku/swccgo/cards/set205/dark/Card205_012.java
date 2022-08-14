package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopThreeCardsOfReserveDeckAndChooseCardToPlaceInUsedPileLostPileAndHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NeverDeploysToLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Imperial
 * Title: Emperor Palpatine, Foreseer
 */
public class Card205_012 extends AbstractDarkJediMasterImperial {
    public Card205_012() {
        super(Side.DARK, 1, 5, 4, 7, 9, "Emperor Palpatine, Foreseer", Uniqueness.UNIQUE);
        setLore("From his throne room aboard the second Death Star, Emperor Palpatine monitors activity throughout the galaxy. Leader.");
        setGameText("Never deploys or moves (even if carried) to a site opponent occupies. Once during your turn, may peek at the top three cards of your Reserve Deck; place one in Used Pile, one in Lost Pile, and take one into hand. Immune to attrition.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NeverDeploysToLocationModifier(self, siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top three cards of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopThreeCardsOfReserveDeckAndChooseCardToPlaceInUsedPileLostPileAndHandEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
