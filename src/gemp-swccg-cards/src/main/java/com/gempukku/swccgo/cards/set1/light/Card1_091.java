package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.BattleDamageMultiplierModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Got A Bad Feeling About This
 */
public class Card1_091 extends AbstractUsedInterrupt {
    public Card1_091() {
        super(Side.LIGHT, 4, "I've Got A Bad Feeling About This", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Han's smuggling adventures in Corporate Sector and Hutt Space put him in many tight scrapes. He's about to be in another.");
        setGameText("If you just initiated a battle at a location where you have less power than the opponent, double opponent's battle damage if you win the battle (if Han is present at the battle location, triple opponent's battle damage).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // TODO: Troy start here...

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.canTargetCard(self))
                && GameConditions.hasLessPowerInBattleThanOpponent(game, playerId)) {
            final int damageMultiplier = GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Han, Filters.presentInBattle, Filters.canBeTargetedBy(self))) ? 3 : 2;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText(damageMultiplier == 2 ? "Double opponent's battle damage" : "Triple opponent's battle damage");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose battle location", Filters.battleLocation) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedBattleLocation) {
                            action.addAnimationGroup(targetedBattleLocation);
                            if (damageMultiplier == 3) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Han", Filters.and(Filters.Han, Filters.presentInBattle)) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard targetedHan) {
                                                action.addAnimationGroup(targetedHan);
                                                // Allow response(s)
                                                action.allowResponses("Triple opponent's battle damage by targeting " + GameUtils.getCardLink(targetedHan),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                                new BattleDamageMultiplierModifier(self, damageMultiplier, opponent),
                                                                                "Triples opponent's battle damage"));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            } else {
                                // Allow response(s)
                                action.allowResponses("Double opponent's battle damage",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new BattleDamageMultiplierModifier(self, damageMultiplier, opponent),
                                                                "Doubles opponent's battle damage"));
                                            }
                                        }
                                );

                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}