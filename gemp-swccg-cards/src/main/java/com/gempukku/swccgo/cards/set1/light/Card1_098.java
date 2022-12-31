package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Narrow Escape
 */
public class Card1_098 extends AbstractUsedInterrupt {
    public Card1_098() {
        super(Side.LIGHT, 5, Title.Narrow_Escape, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Blast doors seal off compartments during battles, hull ruptures or as security measures. Thick doors repel blaster rifle shots.");
        setGameText("If opponent just initiated battle at a site where you have a Rebel of ability > 2 present, move all of your cards with ability there away (using their landspeed).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.site,
                Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2),
                        Filters.presentInBattle, Filters.canBeTargetedBy(self))), Filters.canBeTargetedBy(self)))
                && GameConditions.canSpotLocation(game, Filters.adjacentSite(game.getGameState().getBattleLocation()))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move cards with ability away");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Rebel of ability > 2", Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2), Filters.presentInBattle)) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedRebel) {
                            action.addAnimationGroup(targetedRebel);
                            // Allow response(s)
                            action.allowResponses("Move cards with ability away by targeting " + GameUtils.getCardLink(targetedRebel),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardsAwayEffect(action, playerId, Filters.and(Filters.your(self),
                                                            Filters.hasAbility, Filters.participatingInBattle, Filters.canBeTargetedBy(self))));
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