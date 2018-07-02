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

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Krayt Dragon Howl
 */
public class Card1_093 extends AbstractLostInterrupt {
    public Card1_093() {
        super(Side.LIGHT, 4, "Krayt Dragon Howl", Uniqueness.UNIQUE);
        setLore("Using Jedi skill, Obi-Wan Kenobi imitated perfectly the mournful howl of the dangerous krayt dragon to scare Tusken Raiders away from Luke Skywalker.");
        setGameText("If Obi-Wan is defending a battle alone at a site, add 1 to power and add one battle destiny. OR If any other Rebel with ability > 2 is defending a battle alone at a site, add one battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final Filter obiWanFilter = Filters.and(Filters.ObiWan, Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, obiWanFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add 1 to power and add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan", obiWanFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard obiWanTargeted) {
                                action.addAnimationGroup(obiWanTargeted);
                                // Allow response(s)
                                action.allowResponses("Add 1 to power and add one battle destiny by targeting " + GameUtils.getCardLink(obiWanTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 1, playerId, "Adds 1 to power"));
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 1));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }

            final Filter rebelFilter = Filters.and(Filters.Rebel, Filters.not(Filters.ObiWan), Filters.abilityMoreThan(2), Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, rebelFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Rebel", rebelFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard rebelTargeted) {
                                action.addAnimationGroup(rebelTargeted);
                                // Allow response(s)
                                action.allowResponses("Add one battle destiny by targeting " + GameUtils.getCardLink(rebelTargeted),
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
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}