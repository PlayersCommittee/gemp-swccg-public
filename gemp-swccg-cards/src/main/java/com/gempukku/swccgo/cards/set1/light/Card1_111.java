package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
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
 * Title: Solo Han
 */
public class Card1_111 extends AbstractLostInterrupt {
    public Card1_111() {
        super(Side.LIGHT, 4, Title.Solo_Han, Uniqueness.UNIQUE);
        setLore("As a smuggler, Solo must always be alert for trouble, even when appearing relaxed. Bounty hunters are eager to claim the price on the Corellian pirate's head.");
        setGameText("If Han is defending a battle alone at a site, add two battle destiny. OR If any alien is defending a battle alone at a site, add one battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            Filter hanFilter = Filters.and(Filters.Han, Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, hanFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Han", hanFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard hanTargeted) {
                                action.addAnimationGroup(hanTargeted);
                                // Allow response(s)
                                action.allowResponses("Add two battle destiny by targeting " + GameUtils.getCardLink(hanTargeted),
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

            Filter alienFilter = Filters.and(Filters.your(self), Filters.alien, Filters.defendingBattle, Filters.alone);
            if (GameConditions.canTarget(game, self, alienFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose alien", alienFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard alienTargeted) {
                                action.addAnimationGroup(alienTargeted);
                                // Allow response(s)
                                action.allowResponses("Add one battle destiny by targeting " + GameUtils.getCardLink(alienTargeted),
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
        }
        return actions;
    }
}