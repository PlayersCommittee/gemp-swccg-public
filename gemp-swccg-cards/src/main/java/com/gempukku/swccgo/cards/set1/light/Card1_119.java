package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Warrior's Courage
 */
public class Card1_119 extends AbstractLostInterrupt {
    public Card1_119() {
        super(Side.LIGHT, 4, Title.Warriors_Courage, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Each Rebel soldier is driven by bravery and a belief in the Rebel Alliance's ideal of freedom. Courageous and quick-thinking Rebels often defeat Imperial legions.");
        setGameText("If your warrior is defending a battle alone at a site, add one battle destiny. OR If Leia is defending a battle alone at a site, add two battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            Filter warriorFilter = Filters.and(Filters.your(self), Filters.warrior, Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, warriorFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose warrior", warriorFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard warriorTargeted) {
                                action.addAnimationGroup(warriorTargeted);
                                // Allow response(s)
                                action.allowResponses("Add one battle destiny by targeting " + GameUtils.getCardLink(warriorTargeted),
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


            Filter leiaFilter = Filters.and(Filters.Leia, Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, leiaFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Leia", leiaFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard leiaTargeted) {
                                action.addAnimationGroup(leiaTargeted);
                                // Allow response(s)
                                action.allowResponses("Add two battle destiny by targeting " + GameUtils.getCardLink(leiaTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 2));
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