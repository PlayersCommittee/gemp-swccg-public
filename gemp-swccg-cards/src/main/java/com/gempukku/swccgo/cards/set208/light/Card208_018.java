package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Lost
 * Title: Corellian Slip (V)
 */
public class Card208_018 extends AbstractLostInterrupt {
    public Card208_018() {
        super(Side.LIGHT, 4, Title.Corellian_Slip);
        setVirtualSuffix(true);
        setLore("First perfected by Corellian starship battle tacticians, this dangerous counter-maneuver has saved numerous hot-shot pilots in life-or-death situations.");
        setGameText("During battle, target opponent's starfighter with your snub fighter. Starfighter's gametext (except related to any capacity and the ability and identify of any permanent pilots) is canceled. OR If your pilot of ability < 3 was just lost from aboard a snub fighter, take pilot into hand.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, Filters.and(Filters.opponents(self), Filters.starfighter,
                Filters.inBattleWith(Filters.and(Filters.your(self), Filters.snub_fighter, Filters.canBeTargetedBy(self)))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel a starfighter's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", Filters.and(Filters.opponents(self), Filters.starfighter, Filters.participatingInBattle)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedStarfighter) {
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose snub fighter", Filters.and(Filters.your(self), Filters.snub_fighter, Filters.participatingInBattle)) {
                                        @Override
                                        protected boolean getUseShortcut() {
                                            return true;
                                        }

                                        @Override
                                        protected void cardTargeted(int targetGroupId2, final PhysicalCard targetedSnubFighter) {
                                            action.addAnimationGroup(targetedStarfighter, targetedSnubFighter);
                                            // Allow response(s)
                                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedStarfighter) + "'s game text by targeting " + GameUtils.getCardLink(targetedSnubFighter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the final targeted card(s)
                                                            final PhysicalCard finalStarfighter = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new CancelGameTextEffect(action, finalStarfighter));
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
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justLostFromAttachedTo(game, effectResult, Filters.and(Filters.your(self), Filters.pilot, Filters.abilityLessThan(3)), Filters.snub_fighter)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take " + GameUtils.getFullName(justLostCard) + " into hand");
            // Allow response(s)
            action.allowResponses("Take " + GameUtils.getCardLink(justLostCard) + " into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, justLostCard, false, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}