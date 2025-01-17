package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsAttritionEffect;
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
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: This is MY Ship!
 */
public class Card207_015 extends AbstractUsedOrLostInterrupt {
    public Card207_015() {
        super(Side.LIGHT, 4, Title.This_Is_My_Ship, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setGameText("USED: If Hera is piloting Ghost in battle, draw one destiny and subtract it from opponent's attrition against you. LOST: If Chopper, Ezra, Hera, Kanan, Sabine, or Zeb in a battle, they each add 1 to your total battle destiny.");
        addIcons(Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.Ghost, Filters.hasPiloting(self, Filters.Hera), Filters.participatingInBattle, Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Reduce opponent's attrition");
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
                                                            new SubtractFromOpponentsAttritionEffect(action, totalDestiny));
                                                }
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            final Filter inBattleFilter = Filters.and(Filters.or(Filters.Chopper, Filters.Ezra, Filters.Hera, Filters.Kanan, Filters.Sabine, Filters.Zeb), Filters.participatingInBattle);
            int count = Filters.countActive(game, self, inBattleFilter);
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Add " + count + " to total battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                final int numToAdd = Filters.countActive(game, self, inBattleFilter);
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, numToAdd));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}