package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Radar Scanner
 */
public class Card1_104 extends AbstractUsedInterrupt {
    public Card1_104() {
        super(Side.LIGHT, 3, Title.Radar_Scanner);
        setLore("Sensor on Luke's landspeeder. Many possible settings. Can scan for life forms, movement or concentrations of metal. Used for traffic control on settled worlds.");
        setGameText("If you have at least one vehicle or starship on table, use 1 Force to glance at the cards in the opponent's hand for 10 seconds. You may move each Jawa (except Dathcha) and Tusken Raider you find there to opponent's Used Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter vehicleOrStarshipFilter = Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, vehicleOrStarshipFilter)
                && GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at opponent's hand");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle or starship", vehicleOrStarshipFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard vehicleOrStarshipTargeted) {
                            action.addAnimationGroup(vehicleOrStarshipTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Peek at opponent's hand by targeting " + GameUtils.getCardLink(vehicleOrStarshipTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PeekAtOpponentsHandEffect(action, playerId) {
                                                        @Override
                                                        protected void cardsPeekedAt(final List<PhysicalCard> peekedAtCards) {
                                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RADAR_SCANNER__JAWAS_TUSKEN_RAIDERS_AND_STORMTROOPERS_LOST)) {
                                                                Collection<PhysicalCard> cardsToLose = Filters.filter(peekedAtCards, game, Filters.and(Filters.or(Filters.Jawa, Filters.Tusken_Raider, Filters.stormtrooper), Filters.canBeTargetedBy(self)));
                                                                if (!cardsToLose.isEmpty()) {
                                                                    action.appendEffect(
                                                                            new LoseCardsFromHandEffect(action, opponent, cardsToLose));
                                                                }
                                                            }
                                                            ;
                                                            action.appendEffect(
                                                                    new PassthruEffect(action) {
                                                                        @Override
                                                                        protected void doPlayEffect(final SwccgGame game) {
                                                                            final Filter jawaAndTuskenRaiderFilter = Filters.and(Filters.or(Filters.and(Filters.Jawa, Filters.except(Filters.Dathcha)), Filters.Tusken_Raider), Filters.canBeTargetedBy(self));
                                                                            if (GameConditions.hasInHand(game, opponent, jawaAndTuskenRaiderFilter)) {
                                                                                action.appendEffect(
                                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                                new YesNoDecision("Do you want to place Jawas and Tusken Raiders from opponent's hand in Used Pile?") {
                                                                                                    @Override
                                                                                                    protected void yes() {
                                                                                                        game.getGameState().sendMessage(playerId + " chooses to place Jawas and Tusken Raiders in Used Pile");
                                                                                                        action.appendEffect(
                                                                                                                new PutCardsFromHandOnUsedPileEffect(action, playerId, opponent, jawaAndTuskenRaiderFilter, false));
                                                                                                    }

                                                                                                    @Override
                                                                                                    protected void no() {
                                                                                                        game.getGameState().sendMessage(playerId + " chooses to not place Jawas and Tusken Raiders in Used Pile");
                                                                                                    }
                                                                                                }
                                                                                        )
                                                                                );
                                                                            }
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