package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jabba's Palace Sabacc
 */
public class Card6_067 extends AbstractLostInterrupt {
    public Card6_067() {
        super(Side.LIGHT, 3, "Jabba's Palace Sabacc", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Jabba could lick anyone at sabacc.");
        setGameText("Requirements: A gambler, gangster, smuggler or information broker at a Jabba's Palace site. Wild cards (1-6): Passenger Deck and deserts. Clone cards: Aliens and Jabba's Palace sites (gamblers and Jabba may use clone cards as 4's.) Stakes: One character weapon or non-unique alien.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter characterFilter = Filters.and(Filters.or(Filters.gambler, Filters.gangster, Filters.smuggler, Filters.information_broker), Filters.at(Filters.Jabbas_Palace_site));

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
                                                            Filter wildCards = Filters.or(Filters.Passenger_Deck, Filters.desert);
                                                            Filter cloneCards = Filters.or(Filters.alien, Filters.Jabbas_Palace_site);
                                                            Map<Filterable, Integer> cloneCardPerks = new HashMap<Filterable, Integer>();
                                                            cloneCardPerks.put(Filters.or(Filters.gambler, Filters.Jabba), 4);
                                                            Filter stakes = Filters.or(Filters.character_weapon, Filters.and(Filters.non_unique, Filters.alien));
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