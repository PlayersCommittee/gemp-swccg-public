package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Ascension Guns (V)
 */
public class Card601_172 extends AbstractUsedOrLostInterrupt {
    public Card601_172() {
        super(Side.LIGHT, 5, Title.Ascension_Guns, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Modified S-5 security blasters can be used for a wide array of purposes");
        setGameText("USED: If opponent occupies your site, peek at the top two cards of Reserve Deck and take one into hand. \n" +
                "LOST: During any move phase, lose 2 Force to relocate up to 3 of your characters from an interior Theed Palace site to any related Throne Room (or vice versa).");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)) {
            final PhysicalCard theedPalaceThroneRoom = Filters.findFirstFromTopLocationsOnTable(game, Filters.Theed_Palace_Throne_Room);
            if (theedPalaceThroneRoom != null) {
                Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.interior_Theed_Palace_site, Filters.not(theedPalaceThroneRoom),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.canBeRelocatedToLocation(theedPalaceThroneRoom, true, 0)))));
                if (!fromSites.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action.setText("Relocate characters");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate from", Filters.in(fromSites)) {
                                @Override
                                protected void cardSelected(final PhysicalCard fromSite) {
                                    final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                            Filters.and(Filters.your(self), Filters.character, Filters.at(fromSite), Filters.canBeRelocatedToLocation(theedPalaceThroneRoom, true, 0)));
                                    action.appendTargeting(
                                            new TargetCardsOnTableEffect(action, playerId, "Choose characters to relocate", 1, 3, Filters.in(charactersToRelocate)) {
                                                @Override
                                                protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                    action.addAnimationGroup(cardsToRelocate);
                                                    action.addAnimationGroup(theedPalaceThroneRoom);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new LoseForceEffect(action, playerId, 2));
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

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
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