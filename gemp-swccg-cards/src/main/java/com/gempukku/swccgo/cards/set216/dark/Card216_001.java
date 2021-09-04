package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealCardFromOwnHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Sith Legend
 */
public class Card216_001 extends AbstractLostInterrupt {
    public Card216_001() {
        super(Side.DARK, 2, Title.A_Sith_Legend, Uniqueness.UNIQUE);
        setLore("At his peak, no one could stand up to the Dark Lord of the Sith. His superior tactics devastated those who opposed him.");
        setGameText("Reveal a lightsaber from hand to [upload] a matching Dark Jedi or Sith (or vice versa). " +
                "OR Once per game, cancel the game text of an opponent's non-Jedi character present with your Dark Jedi or Inquisitor for remainder of turn.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.A_SITH_LEGEND__UPLOAD_MATCHING_CARD;

        final Filter lightsaberFilter = Filters.lightsaber;
        final Filter characterFilter = Filters.or(Filters.Dark_Jedi, Filters.Sith);

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, Filters.or(lightsaberFilter, characterFilter))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Reveal a card from hand");
            action.setActionMsg("Reveal a lightsaber from hand to take a matching Dark Jedi or Sith into hand from Reserve Deck (or vice versa)");
            // Allow response(s)
            action.appendTargeting(new ChooseCardFromHandEffect(action, playerId, Filters.or(lightsaberFilter, characterFilter)) {
                @Override
                protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                    action.appendCost(new RevealCardFromOwnHandEffect(action, playerId, selectedCard));
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    //matching
                                    Filter matchingFilter = Filters.and(lightsaberFilter, Filters.matchingWeaponForCharacter(selectedCard));
                                    if (lightsaberFilter.accepts(game, selectedCard)) {
                                        matchingFilter = Filters.and(characterFilter, Filters.matchingCharacter(selectedCard));
                                    }

                                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, matchingFilter, true));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }


        gameTextActionId = GameTextActionId.A_SITH_LEGEND__CANCEL_GAME_TEXT;

        // Check condition(s)
        Filter darkJediOrInquisitor = Filters.and(Filters.your(self), Filters.or(Filters.Dark_Jedi, Filters.inquisitor));
        Filter character = Filters.and(Filters.opponents(self), Filters.non_Jedi_character, Filters.presentWith(self, darkJediOrInquisitor));

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, character)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Cancel game text of a character");
            action.setActionMsg("Cancel the game text of a non-Jedi character present with your Dark Jedi or Inquisitor");

            action.appendUsage(new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character to cancel game text", character) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard characterTargeted) {
                            action.addAnimationGroup(characterTargeted);

                            // Allow response(s)
                            action.allowResponses("Cancel game text of " + GameUtils.getCardLink(characterTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            Collection<PhysicalCard> finalCharacter = action.getPrimaryTargetCards(targetGroupId);

                                            for(PhysicalCard card:finalCharacter) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelGameTextEffect(action, card));
                                            }
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}