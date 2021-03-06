package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.*;

/**
 * Set: Block 7
 * Type: Interrupt
 * Subtype: Used
 * Title: Yub Yub, Commander
 */
public class Card601_063 extends AbstractUsedOrLostInterrupt {
    public Card601_063() {
        super(Side.LIGHT, 5, "Yub Yub, Commander", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Deploy a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot from Reserve Deck; reshuffle. \n" +
                "LOST: Cancel an attempt by opponent to target a Rogue Squadron pilot to be captured or excluded from battle. OR If two Rogue Squadron pilots are in battle together, add one battle destiny.");
        addIcons(Icon.BLOCK_7, Icon.ENDOR);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.Rogue_Squadron_pilot, Filters.canBeTargetedBy(self));
        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_CAPTURED, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, filter, targetingReasons)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter);
            if (!cardsTargeted.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                action.setText("Cancel targeting");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Rogue Squadron pilot", Filters.in(cardsTargeted)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Cancel targeting of " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                PhysicalCard finalPilot = action.getPrimaryTargetCard(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelTargetingEffect(action, respondableEffect));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YUB_YUB_COMMANDER__DEPLOY_ROGUE_SQUADRON_PILOT;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.occupiesWith(playerId, self, Filters.Rogue_Squadron_pilot))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy Rogue Squadron pilot");
            // Pay cost(s)
            // Allow response(s)
            action.allowResponses("Deploy Rogue Squadron pilot" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Rogue_Squadron_pilot, Filters.occupiesWith(playerId, self, Filters.Rogue_Squadron_pilot), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Rogue_Squadron_pilot)
                && GameConditions.canSpot(game, self, 2, Filters.and(Filters.participatingInBattle, Filters.Rogue_Squadron_pilot))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Add battle destiny");
            // Pay cost(s)
            // Allow response(s)
            action.allowResponses("Add battle destiny" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1, playerId));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}