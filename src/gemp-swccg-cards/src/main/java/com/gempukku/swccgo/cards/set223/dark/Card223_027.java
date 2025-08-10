package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RearmCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/* 
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Young Fool (V)
*/

public class Card223_027 extends AbstractUsedOrLostInterrupt {
    public Card223_027() {
        super(Side.DARK, 6, Title.Young_Fool, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Now, young Skywalker... you will die");
        setGameText("USED: If you have two battlegrounds on table and opponent does not, activate up to 2 Force. LOST: Cancel Disarmed or Jedi Presence. OR During your turn, if Sidious in battle and no other Dark Jedi participating, add one destiny to total power.");
        setVirtualSuffix(true);
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        
        int yourBattlegroundCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.your(self), Filters.battleground));
        int opponentBattlegroundCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.opponents(self), Filters.battleground));

        if (GameConditions.canActivateForce(game, playerId)
                && yourBattlegroundCount >= 2
                && opponentBattlegroundCount < 2) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate up to 2 Force");

            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            int maxForceToActivate = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
                            if (maxForceToActivate > 0) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
                                                    @Override
                                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new ActivateForceEffect(action, playerId, result));
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                    }
            );
            actions.add(action);
        }

        final Filter charactersWhoAreDisarmed = Filters.Disarmed;
        if (GameConditions.canTarget(game, self, charactersWhoAreDisarmed)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel Disarmed On This Character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Disarmed Character", charactersWhoAreDisarmed) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Restore " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            //action.appendEffect(action.getPrimaryTargetCard(targetGroupId).setDisarmed(false));
                                            action.appendEffect(new RearmCharacterEffect(action, targetedCard));

                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Jedi_Presence)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Jedi_Presence, Title.Jedi_Presence);
            actions.add(action);
        }

        if (GameConditions.isDuringYourTurn(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Sidious)
                && !GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Dark_Jedi, Filters.not(Filters.Sidious)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add one destiny to total power");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddDestinyToTotalPowerEffect(action, 1));
                            }
                        }
                );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter disarmedFilter = Filters.title(Title.Disarmed);
        Filter jediPresenceFilter = Filters.title(Title.Jedi_Presence);
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(disarmedFilter, jediPresenceFilter))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }

}
