package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Knights Of The Old Republic
 */
public class Card209_020 extends AbstractUsedOrLostInterrupt {
    public Card209_020() {
        super(Side.LIGHT, 5, Title.Knights_Of_The_Old_Republic, Uniqueness.UNIQUE);
        setLore("A Jedi seeks nonviolent solutions to problems, but may fight to preserve the existence of life. An apprentice must learn which battles to fight and which to avoid.");
        setGameText("USED: If present with a Dark Jedi or [Episode I] Jedi, your Padawan is power +2 for remainder of turn. OR Deploy one [Episode I] lightsaber from Reserve Deck; reshuffle. LOST: Once per game, retrieve a Padawan or [Episode I] lightsaber.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_9);
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {

        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter yourPadawan = Filters.and(Filters.your(self), Filters.padawan);
        final Filter ep1Jedi = Filters.and(Icon.EPISODE_I, Filters.Jedi);
        final Filter darkJediOrEp1Jedi  = Filters.or(Filters.Dark_Jedi, ep1Jedi);
        final Filter padawanWithDarkJediOrEp1Jedi = Filters.and(yourPadawan, Filters.presentWith(self, darkJediOrEp1Jedi));
        final Filter ep1Lightsaber = Filters.and(Icon.EPISODE_I, Filters.lightsaber);
        final Filter padawanOrEp1Lightsaber = Filters.or(Filters.padawan, ep1Lightsaber);


        // Check condition(s) - If present with a Dark Jedi or [Episode I] Jedi, your Padawan is power +2 for remainder of turn.
        if (GameConditions.canTarget(game, self, padawanWithDarkJediOrEp1Jedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Make Padawan power +2");

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target your Padawan", padawanWithDarkJediOrEp1Jedi) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power +2'",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)

                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new PowerModifier(self, finalTarget, 2),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " power +2"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }




        // Check condition(s) - Deploy one [Episode I] lightsaber from Reserve Deck;
        GameTextActionId deployLightsaberActionId = GameTextActionId.KNIGHTS_OF_THE_OLD_REPUBLIC_DEPLOY_LIGHTSABER;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployLightsaberActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Deploy [Episode I] lightsaber from Reserve Deck");

            // Allow response(s)
            action.allowResponses("Deploy an [Episode I] lightsaber from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, ep1Lightsaber, true)
                            );
                        }
                    }
            );
            actions.add(action);
        }



        // Check condition(s) - Once per game, retrieve a Padawan or [Episode I] lightsaber.
        GameTextActionId retrieveActionId = GameTextActionId.KNIGHTS_OF_THE_OLD_REPUBLIC_RETRIEVE_PADAWAN_OR_LIGHTSABER;
        if (GameConditions.isOncePerGame(game, self, retrieveActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveActionId, CardSubtype.LOST);
            action.setText("Retrieve a Padawan or [Episode I] lightsaber");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));

            // Allow response(s)
            action.allowResponses("Retrieve a Padawan or [Episode I] lightsaber",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, padawanOrEp1Lightsaber)
                            );
                        }
                    }
            );
            actions.add(action);
        }


        return actions;
    }
}