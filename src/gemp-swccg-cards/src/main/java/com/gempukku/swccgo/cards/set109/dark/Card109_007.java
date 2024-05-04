package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyNumCardsDrawnInStartingHandEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardCombinationIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Any Methods Necessary
 */
public class Card109_007 extends AbstractUsedOrStartingInterrupt {
    public Card109_007() {
        super(Side.DARK, 4, Title.Any_Methods_Necessary, Uniqueness.UNIQUE, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setLore("Darth Vader authorized the bounty hunters to use any means at their disposal to find and capture the Millennium Falcon - not that they need any encouragement.");
        setGameText("USED: Activate 1 Force. STARTING: Take into hand from Reserve Deck one prison and one bounty hunter (may also take a matching weapon and/or starship). When you draw your starting hand, draw six more cards instead of eight. Place Interrupt in Reserve Deck.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 1 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {

        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Take cards into hand from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Take a prison and a bounty hunter (may also take a matching weapon and/or starship) into hand from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new TakeCardCombinationIntoHandFromReserveDeckEffect(action, playerId, false) {
                                    @Override
                                    public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                                        return "Choose a prison and a bounty hunter (may also choose a matching weapon and/or starship)";
                                    }

                                    @Override
                                    public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                                        Filter filter = Filters.none;
                                        if (Filters.filterCount(cardsSelected, game, 1, Filters.prison).isEmpty()) {
                                            filter = Filters.or(filter, Filters.prison);
                                        }
                                        if (Filters.filterCount(cardsSelected, game, 1, Filters.bounty_hunter).isEmpty()) {
                                            filter = Filters.or(filter, Filters.bounty_hunter);
                                        }
                                        else {
                                            PhysicalCard bountyHunter = Filters.filterCount(cardsSelected, game, 1, Filters.bounty_hunter).iterator().next();
                                            Filter matchingWeapon = Filters.matchingWeaponForCharacter(bountyHunter);
                                            Filter matchingStarship = Filters.matchingStarship(bountyHunter);

                                            if (Filters.filterCount(cardsSelected, game, 1, matchingWeapon).isEmpty()) {
                                                filter = Filters.or(filter, matchingWeapon);
                                            }
                                            if (Filters.filterCount(cardsSelected, game, 1, matchingStarship).isEmpty()) {
                                                filter = Filters.or(filter, matchingStarship);
                                            }
                                        }
                                        return filter;
                                    }

                                    @Override
                                    public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                                        if (Filters.filter(cardsSelected, game, Filters.prison).size() != 1
                                                || Filters.filter(cardsSelected, game, Filters.bounty_hunter).size() != 1) {
                                            return false;
                                        }
                                        else if (cardsSelected.size() == 2) {
                                            return true;
                                        }
                                        else {
                                            PhysicalCard bountyHunter = Filters.filterCount(cardsSelected, game, 1, Filters.bounty_hunter).iterator().next();
                                            Filter matchingWeapon = Filters.matchingWeaponForCharacter(bountyHunter);
                                            Filter matchingStarship = Filters.matchingStarship(bountyHunter);

                                            if (cardsSelected.size() == 3
                                                    && (Filters.filter(cardsSelected, game, matchingWeapon).size() == 1
                                                    || Filters.filter(cardsSelected, game, matchingStarship).size() == 1)) {
                                                return true;
                                            }
                                            if (cardsSelected.size() == 4
                                                    && Filters.filter(cardsSelected, game, matchingWeapon).size() == 1
                                                    && Filters.filter(cardsSelected, game, matchingStarship).size() == 1) {
                                                return true;
                                            }
                                        }
                                        return false;
                                    }
                                });
                        action.appendEffect(
                                new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, 6));
                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}