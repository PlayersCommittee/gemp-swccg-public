package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayMoveUsingHyperspeedCostEffect;
import com.gempukku.swccgo.cards.effects.PayMoveUsingSectorMovementCostEffect;
import com.gempukku.swccgo.cards.effects.PayMoveWithoutUsingHyperspeedCostEffect;
import com.gempukku.swccgo.cards.effects.PayTakeOffCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.MoveStarshipUsingHyperspeedEffect;
import com.gempukku.swccgo.logic.effects.MoveStarshipWithoutUsingHyperspeedEffect;
import com.gempukku.swccgo.logic.effects.MoveUsingSectorMovementEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeOffEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: That's It, The Rebels Are There!
 */
public class Card3_137 extends AbstractUsedInterrupt {
    public Card3_137() {
        super(Side.DARK, 4, "That's It, The Rebels Are There!", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U2);
        setLore("'That is the system and I'm sure Skywalker is with them.'");
        setGameText("If you have a probe droid at a site during your control phase, move one of your starships to the related system. That starship cannot move again this turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter relatedSystems = Filters.relatedSystemTo(self,Filters.and(Filters.your(self), Filters.probe_droid, Filters.at(Filters.site)));
        Filter validShips = Filters.and(Filters.your(self),Filters.starship,Filters.movableAsRegularMove(playerId, false, 0, false, relatedSystems));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
            && GameConditions.canTarget(game, self, validShips)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move starship");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", validShips) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard starshipToMove) {
                            Filter systemsInRange = Filters.or(
                                    Filters.canMoveToWithoutUsingHyperspeed(playerId,starshipToMove,false,false,0),
                                    Filters.canMoveToUsingHyperspeed(playerId,starshipToMove,false,false,0),
                                    Filters.canTakeOffToLocation(playerId,starshipToMove,false,false,0),
                                    Filters.canMoveToUsingSectorMovement(playerId,starshipToMove,false,false,0));
                            Filter validSystemsInRange = Filters.and(relatedSystems,systemsInRange);

                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose system for " + GameUtils.getCardLink(starshipToMove) + " to move to", validSystemsInRange) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard systemSelected) {
                                            action.addAnimationGroup(starshipToMove);
                                            action.addAnimationGroup(systemSelected);

                                            //assumes mutually exclusive movement types
                                            //update later if needed to allow choice of movement with/without hyperspeed from Death Star to the system it is orbiting
                                            int moveType; //save movement type selected for pay and move effects later
                                            if(Filters.canMoveToWithoutUsingHyperspeed(playerId,starshipToMove,false,false,0).accepts(game,systemSelected)) moveType = 1;
                                            else if(Filters.canMoveToUsingHyperspeed(playerId,starshipToMove,false,false,0).accepts(game,systemSelected)) moveType = 2;
                                            else if(Filters.canTakeOffToLocation(playerId,starshipToMove,false,false,0).accepts(game,systemSelected)) moveType = 3;
                                            else if(Filters.canMoveToUsingSectorMovement(playerId,starshipToMove,false,false,0).accepts(game,systemSelected)) moveType = 4;
                                            else {
                                                moveType = 0; //unknown, jic?
                                                game.getGameState().sendMessage("Card3_137 Unknown movement type");
                                            }
                                            /// ///////////maybe make user choose which type of movement (incase of with/without hyperspeed option), then save choice and use for cost and move effect
                                            // Pay cost(s)
                                            switch(moveType) {
                                                case 1:
                                                    action.appendCost(
                                                            new PayMoveWithoutUsingHyperspeedCostEffect(action, playerId,starshipToMove,systemSelected,false,0));
                                                    break;
                                                case 2:
                                                    action.appendCost(
                                                            new PayMoveUsingHyperspeedCostEffect(action, playerId,starshipToMove,systemSelected,false,0));
                                                    break;
                                                case 3:
                                                    action.appendCost(
                                                            new PayTakeOffCostEffect(action, playerId,starshipToMove,systemSelected,false,0));
                                                    break;
                                                case 4:
                                                    action.appendCost(
                                                            new PayMoveUsingSectorMovementCostEffect(action, playerId,starshipToMove,systemSelected,false,0));
                                                    break;
                                                default:
                                            }

                                            // Allow response(s)
                                            String readableMoveType = switch (moveType) {
                                                case 1 -> " without hyperspeed";
                                                case 2 -> " using hyperspeed";
                                                case 3 -> " by taking off";
                                                case 4 -> " using sector movement";
                                                default -> "";
                                            };

                                            action.allowResponses("Move " + GameUtils.getCardLink(starshipToMove) + " to " + GameUtils.getCardLink(systemSelected) + readableMoveType,
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            PhysicalCard finalStarship = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            switch(moveType) {
                                                                case 1:
                                                                action.appendEffect(
                                                                        new MoveStarshipWithoutUsingHyperspeedEffect(action, finalStarship, systemSelected,false,false));
                                                                    break;
                                                                case 2:
                                                                action.appendEffect(
                                                                        new MoveStarshipUsingHyperspeedEffect(action, finalStarship, systemSelected,false,false));
                                                                    break;
                                                                case 3:
                                                                action.appendEffect(
                                                                        new TakeOffEffect(action, finalStarship, systemSelected,false,false));
                                                                    break;
                                                                case 4:
                                                                action.appendEffect(
                                                                        new MoveUsingSectorMovementEffect(action, finalStarship, systemSelected,false,false));
                                                                    break;
                                                                default:
                                                            }
                                                            action.appendEffect(
                                                                    new MayNotMoveUntilEndOfTurnEffect(action, finalStarship));
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