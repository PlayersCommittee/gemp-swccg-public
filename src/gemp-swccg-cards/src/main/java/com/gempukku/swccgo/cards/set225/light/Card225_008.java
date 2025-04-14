package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerAndAttritionEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: I Don't Need Their Scum, Either (V)
 */
public class Card225_008 extends AbstractUsedOrLostInterrupt {
    public Card225_008() {
        super(Side.LIGHT, 5, Title.I_Dont_Need_Their_Scum_Either, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Of all the scum and villainy Lando had dealt with (pirates, smugglers, con-artists, thieves, swindlers, politicians and Imperial lackeys), he hated bounty hunters the most.");
        setGameText("USED: [Upload] Houjix, Ounee Ta, or Scanner Techs. OR Cancel a Force drain initiated by a lone Slave I. LOST: If an Imperial and a bounty hunter are in a battle together, draw one destiny and subtract that amount from opponent's attrition and total power.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.I_DONT_NEED_THEIR_SCUM_EITHER_V__UPLOAD_CARD;
        
        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Houjix, Ounee Ta, or Scanner Techs into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Houjix, Filters.Ounee_Ta, Filters.Scanner_Techs), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canCancelForceDrain(game, self)) {

            // the Slave I needs to be owned by the force draining player but we can't assume it is owned by the opponent
            String forceDrainingPlayer = game.getGameState().getForceDrainState().getPlayerId();

            if (forceDrainingPlayer != null
                    && TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.sameLocationAs(self, Filters.and(Filters.alone, Filters.Slave_I, Filters.owner(forceDrainingPlayer))))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Cancel Force drain");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelForceDrainEffect(action));
                            }
                        }
                );
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Imperial)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.bounty_hunter)) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Reduce opponent's attrition and total power");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DrawDestinyEffect(action, playerId, 1) {
                                            @Override
                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                if (totalDestiny != null && totalDestiny > 0) {
                                                    action.appendEffect(
                                                            new SubtractFromOpponentsTotalPowerAndAttritionEffect(action, totalDestiny));
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}