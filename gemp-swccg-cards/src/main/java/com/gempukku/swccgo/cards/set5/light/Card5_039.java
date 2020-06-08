package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaySabaccEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Cloud City Sabacc
 */
public class Card5_039 extends AbstractUsedInterrupt {
    public Card5_039() {
        super(Side.LIGHT, 3, Title.Cloud_City_Sabacc, Uniqueness.UNIQUE);
        setLore("The current administrator of Cloud City was rumored to have won his position by playing this version of sabacc.");
        setGameText("Requirements: a gambler, thief or smuggler on Cloud City. Wild cards (1-6): Lando and Weather Vane. Clone cards: Locations and Ugnaughts. (Gamblers may use clone cards as zeroes.) Stakes: One starfighter, weapon or device.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter characterFilter = Filters.and(Filters.or(Filters.gambler, Filters.thief, Filters.smuggler), Filters.on_Cloud_City);

        // Check condition(s)
        if (GameConditions.canPlaySabacc(game)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), characterFilter))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Play sabacc");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to play sabacc", Filters.and(Filters.your(self), characterFilter)) {
                        @Override
                        protected void cardSelected(final PhysicalCard playersCharacter) {
                            // After this point do not allow action to be aborted
                            action.setAllowAbort(false);
                            action.appendTargeting(
                                    new ChooseCardsOnTableEffect(action, opponent, "Choose character to play sabacc", 0, 1, Filters.and(Filters.opponents(self), characterFilter)) {
                                        @Override
                                        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                                            String actionText;
                                            final PhysicalCard opponentsCharacter = selectedCards.size() == 1 ? selectedCards.iterator().next() : null;
                                            if (opponentsCharacter != null) {
                                                action.addAnimationGroup(playersCharacter, opponentsCharacter);
                                                actionText = "Have " + GameUtils.getCardLink(playersCharacter) + " play sabacc against " + GameUtils.getCardLink(opponentsCharacter);
                                            } else {
                                                action.addAnimationGroup(playersCharacter);
                                                actionText = "Have " + GameUtils.getCardLink(playersCharacter) + " play sabacc against an unseen adversary";
                                            }
                                            // Allow response(s)
                                            action.allowResponses(actionText,
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            Filter wildCards = Filters.or(Filters.Lando, Filters.Weather_Vane);
                                                            Filter cloneCards = Filters.or(Filters.location, Filters.Ugnaught);
                                                            Map<Filterable, Integer> cloneCardPerks = new HashMap<Filterable, Integer>();
                                                            cloneCardPerks.put(Filters.gambler, 0);
                                                            Filter stakes = Filters.or(Filters.starfighter, Filters.weapon, Filters.device);
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new PlaySabaccEffect(action, playersCharacter, opponentsCharacter, wildCards, 1, 6, cloneCards, cloneCardPerks, stakes));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}