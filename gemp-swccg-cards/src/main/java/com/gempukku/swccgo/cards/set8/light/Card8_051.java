package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Fly Casual
 */
public class Card8_051 extends AbstractUsedOrLostInterrupt {
    public Card8_051() {
        super(Side.LIGHT, 6, "Fly Casual", Uniqueness.UNIQUE);
        setLore("As a smuggler, Han had years of experience at avoiding Imperial detection. He chose the approach to Endor's moon as the time to pass some of that knowledge on to Chewie.");
        setGameText("USED: Cancel Early Warning Network or It's An Older Code. OR During your deploy phase, deploy one starship (deploy -1) and/or one pilot to a system even without presence or Force icons. LOST: Take one Tydirium or Tantive IV into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Early_Warning_Network, Filters.Its_An_Older_Code))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Early_Warning_Network)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Early_Warning_Network, Title.Early_Warning_Network);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_An_Older_Code)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_An_Older_Code, Title.Its_An_Older_Code);
            actions.add(action);
        }

        // Check condition(s)
        final DeploymentRestrictionsOption deploymentRestrictionsOption = DeploymentRestrictionsOption.evenWithoutPresenceOrForceIcons();
        final Filter starshipOrPilotFilter = Filters.or(Filters.and(Filters.starship, Filters.deployableToLocation(self, Filters.system, false, false, -1, null, deploymentRestrictionsOption, null)),
                Filters.and(Filters.pilot, Filters.deployableToLocation(self, Filters.system, false, false, 0, null, deploymentRestrictionsOption, null)));
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, starshipOrPilotFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Deploy a starship or pilot from hand");
            // Allow response(s)
            action.allowResponses("Deploy a starship or pilot to a system even without presence or Force icons",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromHandEffect(action, playerId, starshipOrPilotFilter, true) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            PlayCardAction playCardAction = selectedCard.getBlueprint().getPlayCardAction(playerId, game, selectedCard, self, false,
                                                    Filters.starship.accepts(game, selectedCard) ? -1 : 0, null, deploymentRestrictionsOption, null, null, null, false, 0, Filters.system, null);
                                            if (playCardAction != null) {
                                                action.appendEffect(
                                                        new StackActionEffect(action, playCardAction));
                                            }
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.FLY_CASUAL__UPLOAD_TYDIRIUM_OR_TANTIVE_IV;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Tydirium or Tantive IV into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Tydirium, Filters.Tantive_IV), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}