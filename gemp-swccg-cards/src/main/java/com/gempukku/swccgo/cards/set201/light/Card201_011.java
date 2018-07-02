package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Ascension Guns (V)
 */
public class Card201_011 extends AbstractUsedInterrupt {
    public Card201_011() {
        super(Side.LIGHT, 5, Title.Ascension_Guns, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Modified S-5 security blasters can be used for a wide array of purposes");
        setGameText("During your deploy phase, use X Force (minimum 1) to relocate X of your characters at an interior Theed Palace site to Theed Palace Throne Room. [Immune to Sense] OR If opponent occupies your location, peek at top two cards of your Reserve Deck and take one into hand.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PhysicalCard theedPalaceThroneRoom = Filters.findFirstFromTopLocationsOnTable(game, Filters.Theed_Palace_Throne_Room);
            if (theedPalaceThroneRoom != null) {
                Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.interior_Theed_Palace_site, Filters.not(theedPalaceThroneRoom),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.canBeRelocatedToLocation(theedPalaceThroneRoom, true, 0)))));
                if (!fromSites.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setImmuneTo(Title.Sense);
                    action.setText("Relocate characters");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate from", Filters.in(fromSites)) {
                                @Override
                                protected void cardSelected(final PhysicalCard fromSite) {
                                    int maxCharacters = GameConditions.forceAvailableToUseToPlayInterrupt(game, playerId, self);
                                    final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                            Filters.and(Filters.your(self), Filters.character, Filters.at(fromSite), Filters.canBeRelocatedToLocation(theedPalaceThroneRoom, true, 0)));
                                    action.appendTargeting(
                                            new TargetCardsOnTableEffect(action, playerId, "Choose characters to relocate", 1, maxCharacters, Filters.in(charactersToRelocate)) {
                                                @Override
                                                protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                    action.addAnimationGroup(cardsToRelocate);
                                                    action.addAnimationGroup(theedPalaceThroneRoom);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new UseForceEffect(action, playerId, cardsToRelocate.size()));
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(theedPalaceThroneRoom),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacters, theedPalaceThroneRoom));
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.and(Filters.your(self), Filters.location, Filters.occupies(opponent)))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top two cards of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Peek at top two cards of Reserve Deck and take one into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}