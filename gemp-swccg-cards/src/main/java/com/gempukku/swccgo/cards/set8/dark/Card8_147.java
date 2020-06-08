package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: Imperial Tyranny
 */
public class Card8_147 extends AbstractLostInterrupt {
    public Card8_147() {
        super(Side.DARK, 4, Title.Imperial_Tyranny, Uniqueness.UNIQUE);
        setLore("The Empire considers alien species to be inferior.");
        setGameText("If an opponent's alien is defending a battle against your non-unique Imperial, add one battle destiny. If an opponent's Ewok, Elom or operative is defending, also add 3 to your total power. OR Add 2 to your total weapon destiny when targeting a non-unique alien.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.non_unique, Filters.Imperial))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.alien, Filters.defendingBattle))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.or(Filters.Ewok, Filters.Elom, Filters.operative), Filters.defendingBattle))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny and add 3 to total power");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 3, playerId, "Adds 3 to total power"));
                            }
                        }
                );
                actions.add(action);
            }
            else {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.non_unique, Filters.alien), Filters.and(Filters.your(self), Filters.weapon))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 2 to total weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect(action, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}