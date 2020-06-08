package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
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

import java.util.*;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Trooper Sabacc
 */
public class Card7_109 extends AbstractUsedInterrupt {
    public Card7_109() {
        super(Side.LIGHT, 4, "Trooper Sabacc", Uniqueness.UNIQUE);
        setLore("Troopers stationed at various outposts play this sabacc variant to pass the time between assignments.");
        setGameText("Requirements: A gambler or trooper at a site. Wild cards: (2-7): Imperial and Rebel leaders. (Troopers may use weapons as wild cards.) Clone cards: Locations and droids. Stakes: One transport vehicle without armor or one character weapon.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter characterFilter = Filters.and(Filters.or(Filters.gambler, Filters.trooper), Filters.at(Filters.site));

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
                                                            Filter wildCards = Filters.and(Filters.or(Filters.Imperial, Filters.Rebel), Filters.leader);
                                                            Map<Filterable, Filterable> wildCardPerks = new HashMap<Filterable, Filterable>();
                                                            wildCardPerks.put(Filters.trooper, Filters.weapon);
                                                            Filter cloneCards = Filters.or(Filters.location, Filters.droid);
                                                            Filter stakes = Filters.or(Filters.and(Filters.transport_vehicle, Filters.hasNoArmor), Filters.character_weapon);
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new PlaySabaccEffect(action, playersCharacter, opponentsCharacter, wildCards, 2, 7, wildCardPerks, cloneCards, stakes));
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