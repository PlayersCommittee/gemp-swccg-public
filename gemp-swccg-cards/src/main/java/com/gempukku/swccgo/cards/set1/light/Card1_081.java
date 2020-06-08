package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Full Throttle
 */
public class Card1_081 extends AbstractLostInterrupt {
    public Card1_081() {
        super(Side.LIGHT, 4, "Full Throttle", Uniqueness.UNIQUE);
        setLore("Rebel pilots use visual scanning to supplement sensors for an edge against Imperial fighter pilots. Natural instincts allow lone Rebels to overcome superior numbers.");
        setGameText("If your pilot (or permanent pilot) is defending a battle alone at a system or sector, add one battle destiny. OR If Luke is defending a battle alone at a system or sector, add 1 to power and add one battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.and(Filters.system_or_sector, Filters.canBeTargetedBy(self)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            final Filter pilotFilter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.pilot, Filters.alone), Filters.permanentPilotAlone), Filters.defendingBattle);
            if (GameConditions.canTarget(game, self, pilotFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose pilot (or permanent pilot)", pilotFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard pilotTargeted) {
                                action.addAnimationGroup(pilotTargeted);
                                // Set secondary target filter(s)
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Add one battle destiny by targeting " + GameUtils.getCardLink(pilotTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 1));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }

            final Filter lukeFilter = Filters.and(Filters.your(self), Filters.and(Filters.Luke, Filters.alone, Filters.defendingBattle));
            if (GameConditions.canTarget(game, self, lukeFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add 1 to power and add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard lukeTargeted) {
                                action.addAnimationGroup(lukeTargeted);
                                // Set secondary target filter(s)
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Add 1 to power and add one battle destiny by targeting " + GameUtils.getCardLink(lukeTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 1, playerId, "Adds 1 to total power"));
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 1));
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