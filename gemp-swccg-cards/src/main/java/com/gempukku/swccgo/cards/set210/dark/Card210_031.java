package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;

/**
 * Set: Set 10
 * Type: Effect
 * Title: The Dark Path (V)
 */
public class Card210_031 extends AbstractNormalEffect {
    public Card210_031() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Dark Path");
        setLore("'If once you start down the dark path, forever will it dominate your destiny. Consume you it will, as it did Obi-Wan's apprentice.'");
        setGameText("Deploy on table. Add one [DS] icon at each site where you have a 'Hatred’ card. Once per game, target two battleground sites you occupy; one at a time, stack top card of your Lost Pile face down (as a 'Hatred’ card) at those locations. [Immune to Alter.]");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_10);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        // Add one [DS] icon at each site where you have a 'Hatred’ card
        Filter siteWithHatredCardStacked = Filters.and(Filters.site, Filters.or(Filters.hasStacked(Filters.hatredCard), Filters.sameSiteAs(self, Filters.and(Filters.character, Filters.hasStacked(Filters.hatredCard)))));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, siteWithHatredCardStacked, Icon.DARK_FORCE, 1));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.THE_DARK_PATH_V__STACK_HATE_CARDS;

        // Once per game, target two battleground sites you occupy;
        // one at a time, stack top card of your Lost Pile face down (as a 'Hatred’ card) at those locations.
        Filter battlegroundsYouOccupy = Filters.and(Filters.battleground_site, Filters.occupies(playerId));

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)
                && game.getGameState().getLostPile(playerId).size() >= 2
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, 2, battlegroundsYouOccupy)) {

            final SwccgGame thisGame = game;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack top cards from Lost Pile on sites");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Target 2 battlegrounds you occupy", 2, 2, battlegroundsYouOccupy) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> cardsTargeted) {
                            action.addAnimationGroup(cardsTargeted);
                            // Allow response(s)
                            action.allowResponses("Battlegrounds targeted",// + GameUtils.getAppendedNames(cardsTargeted),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {

                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            Collection<PhysicalCard> finalTargets = targetingAction.getPrimaryTargetCards(targetGroupId);

                                            // Sanity-checking. Make sure all the cards we spotted are still around
                                            final PhysicalCard topCardOfLostPile = thisGame.getGameState().getTopOfLostPile(playerId);
                                            if (topCardOfLostPile == null) {
                                                // Should never happen. We spotted a lost pile earlier
                                                return;
                                            }

                                            // Get the two battlegrounds we targeted
                                            Iterator<PhysicalCard> iterator = finalTargets.iterator();
                                            final PhysicalCard battleground1 = iterator.next();
                                            if (battleground1 == null) {
                                                // Should never happen because we targeted 2
                                                return;
                                            }

                                            final PhysicalCard battleground2 = iterator.next();
                                            if (battleground2 == null) {
                                                // Should never happen because we targeted 2
                                                return;
                                            }

                                            // Perform result(s) - Stack first card and set as hatred card
                                            action.appendEffect(
                                                    new StackOneCardFromLostPileEffect(action, topCardOfLostPile, battleground1, true, false, true)
                                            );
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            topCardOfLostPile.setHatredCard(true);
                                                        }
                                                    }
                                            );

                                            // Perform result - Stack 2nd card
                                            // We need to do this as a PassthruEffect because we won't have access to the next 'top'
                                            // card of lost pile until the stacking above is complete
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {

                                                            final PhysicalCard secondTopCardOfLostPile = thisGame.getGameState().getTopOfLostPile(playerId);

                                                            // If there isn't another card available (there was only 1 in lost pile)
                                                            // That's ok, let it go anyway
                                                            if (secondTopCardOfLostPile == null) {
                                                                return;
                                                            }

                                                            // Perform results (stack and set as hatred card)
                                                            action.appendEffect(
                                                                    new StackOneCardFromLostPileEffect(action, secondTopCardOfLostPile, battleground2, true, false, true)
                                                            );
                                                            action.appendEffect(
                                                                    new PassthruEffect(action) {
                                                                        @Override
                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                            secondTopCardOfLostPile.setHatredCard(true);
                                                                        }
                                                                    }
                                                            );
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