package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Glancing Blow
 */
public class Card5_049 extends AbstractLostInterrupt {
    public Card5_049() {
        super(Side.LIGHT, 3, Title.Glancing_Blow, Uniqueness.UNIQUE);
        setLore("It had been decades since Vader had felt the sting of an enemy's blade.");
        setGameText("If you have a character with a lightsaber present in a battle with Vader, lose 1 Force to reduce Vader's power to zero. OR If you have a character with a lightsaber in a duel, cancel one opponent's destiny draw immediately after it is revealed.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
         // Check condition(s)
         if (GameConditions.isDuringBattle(game)
                 && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber, Filters.presentInBattle))) {
             Filter vaderFilter = Filters.and(Filters.Vader, Filters.participatingInBattle);
             if (GameConditions.canTarget(game, self, vaderFilter)) {

                 final PlayInterruptAction action = new PlayInterruptAction(game, self);
                 action.setText("Reset Vader's power to zero");
                 // Choose target(s)
                 action.appendTargeting(
                         new TargetCardOnTableEffect(action, playerId, "Choose vader", vaderFilter) {
                             @Override
                             protected void cardTargeted(final int targetGroupId, PhysicalCard vader) {
                                 action.addAnimationGroup(vader);
                                 // Pay cost(s)
                                 action.appendCost(
                                         new LoseForceEffect(action, playerId, 1, true));
                                 // Allow response(s)
                                 action.allowResponses("Reduce " + GameUtils.getCardLink(vader) + "'s power to 0",
                                         new RespondablePlayCardEffect(action) {
                                             @Override
                                             protected void performActionResults(Action targetingAction) {
                                                 // Get the targeted card(s) from the action using the targetGroupId.
                                                 // This needs to be done in case the target(s) were changed during the responses.
                                                 final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                 // Perform result(s)
                                                 action.appendEffect(
                                                         new ResetPowerEffect(action, finalTarget, 0));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDuelDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isDuringDuelWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel duel destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}