package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Fall Back
 */
public class Card3_042 extends AbstractLostInterrupt {
    public Card3_042() {
        super(Side.LIGHT, 5, "Fall Back!", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("'K-one zero...all troops disengage.'");
        setGameText("If opponent just initiated a battle at an exterior site with more than double your total power, use 1 Force to target an adjacent site where opponent has no presence. All your characters present in battle move away (for free) to the target site. The battle is canceled.");
        addIcon(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        final Filter yourCharactersPresentInBattle = Filters.and(Filters.your(self), Filters.character, Filters.presentInBattle, Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.exterior_site,
                Filters.wherePresent(self, yourCharactersPresentInBattle), Filters.canBeTargetedBy(self)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final Filter validSitesAdjacentToBattle = Filters.and(Filters.adjacentSite(game.getGameState().getBattleLocation()),Filters.not(Filters.occupies(opponent)),Filters.canBeTargetedBy(self));

            if (GameConditions.canSpotLocation(game, validSitesAdjacentToBattle)) {
                float playersPower = GameConditions.getBattlePower(game, playerId);
                float opponentsPower = GameConditions.getBattlePower(game, opponent);
                if ((2 * playersPower) < opponentsPower) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Fall back to adjacent site");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose an adjacent site", validSitesAdjacentToBattle) {
                                @Override
                                protected void cardTargeted(final int siteTargetGroupId, PhysicalCard adjacentSite) {
                                    action.addAnimationGroup(adjacentSite);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 1));
                                    // Allow response(s)
                                    action.allowResponses("Move characters away to " + GameUtils.getCardLink(adjacentSite),
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    PhysicalCard finalSite = action.getPrimaryTargetCard(siteTargetGroupId);
                                                    action.appendEffect(
                                                            new MoveCardsAwayEffect(action, playerId, yourCharactersPresentInBattle, finalSite, true, true, false));
                                                    action.appendEffect(
                                                            new CancelBattleEffect(action));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}