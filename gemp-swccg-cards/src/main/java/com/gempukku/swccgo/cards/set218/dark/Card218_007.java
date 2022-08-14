package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used
 * Title: A Dark Time For The Rebellion & Tarkin's Orders
 */
public class Card218_007 extends AbstractUsedInterrupt {
    public Card218_007() {
        super(Side.DARK, 5, "A Dark Time For The Rebellion & Tarkin's Orders", Uniqueness.UNIQUE);
        addComboCardTitles("A Dark Time For The Rebellion", "Tarkin's Orders");
        setGameText("For remainder of turn, opponent's Force retrieval using Resistance characters' game text is canceled. OR For remainder of turn, opponent may not cancel your battle destiny draws. OR Search opponent's Lost Pile; place one device you find there out of play. OR Cancel It Could Be Worse. OR Cancel Projection Of A Skywalker at an opponent's planet site.");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
        action1.setText("Prevent retrieval by Resistance characters");
        // Allow response(s)
        action1.allowResponses("Prevent retrieval from game text of Resistance characters for remainder of turn",
                new RespondablePlayCardEffect(action1) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        final int permCardId = self.getPermanentCardId();
                        final int gameTextSourceCardId = self.getCardId();
                        action1.appendEffect(
                                new AddUntilEndOfTurnActionProxyEffect(action1, new AbstractActionProxy() {

                                    @Override
                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                        final PhysicalCard self = game.findCardByPermanentId(permCardId);
                                        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, game.getOpponent(self.getOwner()))) {
                                            PhysicalCard retrievingCard = ((AboutToRetrieveForceResult)effectResult).getSourceCard();

                                            if (retrievingCard != null && Filters.Resistance_character.accepts(game, retrievingCard)) {

                                                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                action.setText("Cancel retrieval");
                                                action.appendEffect(
                                                        new CancelForceRetrievalEffect(action)
                                                );
                                                return Collections.singletonList((TriggerAction) action);
                                            }
                                        }
                                        return null;
                                    }
                                }));
                    }
                }
        );
        actions.add(action1);


        final PlayInterruptAction protectBattleDestinyDrawsAction = new PlayInterruptAction(game, self);
        protectBattleDestinyDrawsAction.setText("Affect battle destiny draws");
        protectBattleDestinyDrawsAction.setActionMsg("Prevent opponent from canceling your battle destiny draws for remainder of turn");

        // Allow response(s)
        protectBattleDestinyDrawsAction.allowResponses(
                new RespondablePlayCardEffect(protectBattleDestinyDrawsAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        protectBattleDestinyDrawsAction.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(protectBattleDestinyDrawsAction,
                                        new MayNotCancelBattleDestinyModifier(self, playerId, opponent),
                                        "Prevent "+opponent+" from canceling "+playerId+"'s battle destiny draws")
                        );
                    }
                }
        );
        actions.add(protectBattleDestinyDrawsAction);


        GameTextActionId gameTextActionId = GameTextActionId.OMMNI_BOX_ITS_WORSE_V__SEARCH_LOST_PILE;

        if (GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Search opponent's Lost Pile");
            action.allowResponses("Place a device out of play from opponent's Lost Pile", new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(
                            new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, opponent, Filters.device, false));
                }
            });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.It_Could_Be_Worse)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.It_Could_Be_Worse, Title.It_Could_Be_Worse);
            actions.add(action);
        }


        if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_CANCELED, Filters.and(Filters.title(Title.Projection_Of_A_Skywalker), Filters.attachedTo(Filters.and(Filters.opponents(self), Filters.planet_site))))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.and(Filters.title(Title.Projection_Of_A_Skywalker), Filters.attachedTo(Filters.and(Filters.opponents(self), Filters.planet_site))), Title.Projection_Of_A_Skywalker);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.It_Could_Be_Worse)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }
}