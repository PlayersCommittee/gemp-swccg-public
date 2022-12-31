package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Don't Get Cocky
 */
public class Card1_076 extends AbstractLostInterrupt {
    public Card1_076() {
        super(Side.LIGHT, 5, Title.Dont_Get_Cocky, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Luke and Han made an effective team when defending the Millennium Falcon with its quad laser cannons against attacking TIE fighters. 'Great kid! Don't get cocky.'");
        setGameText("If Luke and Han are in a battle together, you may add two battle destiny. OR If opponent just initiated a battle at a system or sector, choose one TIE/ln present to be lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Han)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            final Filter lukeFilter = Filters.and(Filters.Luke, Filters.participatingInBattle);
            final Filter hanFilter = Filters.and(Filters.Han, Filters.participatingInBattle);
            if (GameConditions.canTarget(game, self, lukeFilter)
                    && GameConditions.canTarget(game, self, hanFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard lukeTargeted) {
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Han", hanFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard hanTargeted) {
                                                action.addAnimationGroup(lukeTargeted, hanTargeted);
                                                // Allow response(s)
                                                action.allowResponses("Add two battle destiny by targeting " + GameUtils.getAppendedNames(Arrays.asList(lukeTargeted, hanTargeted)),
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
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        final Filter tieFilter = Filters.and(Filters.TIE_ln, Filters.presentInBattle);
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.system_or_sector, Filters.canBeTargetedBy(self)))
                && GameConditions.canTarget(game, self, targetingReason, tieFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make a TIE/ln lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a TIE/ln", Integer.MAX_VALUE, true, targetingReason, tieFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.battleLocation);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalTie = action.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalTie));
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