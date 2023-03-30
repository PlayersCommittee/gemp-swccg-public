package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Dune Sea Sabacc
 */
public class Card6_065 extends AbstractUsedInterrupt {
    public Card6_065() {
        super(Side.LIGHT, 5, Title.Dune_Sea_Sabacc, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("R'kik D'nec remains undefeated in this version of sabacc. Or at least there are no witnesses to the contrary.");
        setGameText("Requirements: A Jawa at a Tatooine site. Wild cards (0-7): sandcrawler sites, Magnetic Suction Tube, Jawa Siesta and Jawa Pack. Clone cards: Tatooine locations, sandcrawlers, Jawa weapons, and Utinni! Stakes: One device or droid with out armor.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter characterFilter = Filters.and(Filters.Jawa, Filters.at(Filters.Tatooine_site));

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
                                                            Filter wildCards = Filters.or(Filters.sandcrawler_site, Filters.Magnetic_Suction_Tube, Filters.Jawa_Siesta, Filters.Jawa_Pack);
                                                            Filter cloneCards = Filters.or(Filters.Tatooine_location, Filters.sandcrawler, Filters.Jawa_weapon, Filters.Utinni);
                                                            Filter stakes = Filters.or(Filters.device, Filters.and(Filters.droid, Filters.hasNoArmor));
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new PlaySabaccEffect(action, playersCharacter, opponentsCharacter, wildCards, 0, 7, cloneCards, stakes));
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