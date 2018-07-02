package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Lathe
 */
public class Card11_059 extends AbstractAlien {
    public Card11_059() {
        super(Side.DARK, 2, 3, 4, 2, 3, "Lathe", Uniqueness.UNIQUE);
        setLore("Was one of Jabba's vile Nikto henchmen. Leaving Tatooine to see what the galaxy has to offer. Deep down, he loathes the slave trade. Information broker.");
        setGameText("Adds 2 to power of anything he pilots. If opponent has 2 or more aliens of the same title at this site, may place Lathe in Lost Pile to cause all of them to be lost. Once per turn, may use 1 Force to shuffle opponent's Reserve Deck.");
        addIcons(Icon.TATOOINE, Icon.PILOT);
        addKeywords(Keyword.INFORMATION_BROKER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter filter = Filters.and(Filters.opponents(self), Filters.alien, Filters.atSameSite(self),
                Filters.sameTitleAs(self, Filters.and(Filters.opponents(self), Filters.alien, Filters.atSameSite(self))));
        String opponent = game.getOpponent(playerId);

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter)) {
            Collection<PhysicalCard> aliens = Filters.filterActive(game, self, filter);
            if (!aliens.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make aliens with same titles lost");
                action.addAnimationGroup(aliens);
                // Pay cost(s)
                action.appendCost(
                        new LoseCardFromTableEffect(action, self));
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, aliens, true));
                actions.add(action);
            }
        }

        // Card action 2
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, opponent)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Shuffle opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, opponent));
            actions.add(action);
        }

        return actions;
    }
}
