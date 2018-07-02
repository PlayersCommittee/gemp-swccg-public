package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Force Is Strong With This One
 */
public class Card1_116 extends AbstractLostInterrupt {
    public Card1_116() {
        super(Side.LIGHT, 5, Title.The_Force_Is_Strong_With_This_One);
        setLore("Luke's piloting skills and Force abilities made his X-wing a difficult target for Darth Vader as they raced down the Death Star trench.");
        setGameText("If Luke and an Imperial with ability > 2 are involved in the same battle, you may add one battle destiny (add 2 if Imperial is Vader).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Imperial, Filters.abilityMoreThan(2)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            Filter lukeFilter = Filters.and(Filters.Luke, Filters.participatingInBattle);
            final Filter imperialFilter = Filters.and(Filters.Imperial, Filters.abilityMoreThan(2), Filters.participatingInBattle);
            if (GameConditions.canTarget(game, self, lukeFilter)
                    && GameConditions.canTarget(game, self, imperialFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedLuke) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Imperial", imperialFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedImperial) {
                                                action.addAnimationGroup(targetedLuke, targetedImperial);
                                                int numToAdd = Filters.Vader.accepts(game, targetedImperial) ? 2 : 1;
                                                // Allow response(s)
                                                action.allowResponses("Add " + numToAdd + " battle destiny by targeting " + GameUtils.getAppendedNames(Arrays.asList(targetedLuke, targetedImperial)),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the final targeted card(s)
                                                                final PhysicalCard finalImperial = targetingAction.getPrimaryTargetCard(targetGroupId2);
                                                                // Perform result(s)
                                                                int finalNumToAdd = Filters.Vader.accepts(game, finalImperial) ? 2 : 1;
                                                                action.appendEffect(
                                                                        new AddBattleDestinyEffect(action, finalNumToAdd));
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
        }
        return null;
    }
}