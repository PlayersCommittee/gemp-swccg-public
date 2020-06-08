package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsToMoveAwayOrBeLostEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Move Along...
 */
public class Card1_096 extends AbstractUsedInterrupt {
    public Card1_096() {
        super(Side.LIGHT, 3, "Move Along...");
        setLore("Obi-Wan used Jedi 'affect mind' power to convince stormtroopers, 'These aren't the droids you're looking for.'");
        setGameText("Use 1 Force to temporarily suspend a battle just initiated at a site where one of your Jedi is present. Draw destiny. That number of opponent's characters with ability = 1 (your choice) must move away (for free), or are lost. Battle continues.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.site,
                Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Jedi,
                        Filters.canBeTargetedBy(self))), Filters.canBeTargetedBy(self)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make characters move away or be lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Jedi", Filters.and(Filters.your(self), Filters.Jedi, Filters.presentInBattle)) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedJedi) {
                            action.addAnimationGroup(targetedJedi);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Make characters move away or be lost by targeting " + GameUtils.getCardLink(targetedJedi),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            if (totalDestiny == null || totalDestiny == 0) {
                                                                return;
                                                            }
                                                            action.appendEffect(
                                                                    new ChooseCardsToMoveAwayOrBeLostEffect(action, playerId, totalDestiny.intValue(),
                                                                            Filters.and(Filters.opponents(self), Filters.character, Filters.abilityEqualTo(1),
                                                                                    Filters.presentInBattle, Filters.canBeTargetedBy(self))));
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
}