package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Out Of Nowhere
 */
public class Card1_102 extends AbstractUsedInterrupt {
    public Card1_102() {
        super(Side.LIGHT, 5, Title.Out_Of_Nowhere, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Black market sensor-stealth arrays can be used to hide starships from enemy sensor scans. Stellar interference can naturally shield a starship for surprise attacks.");
        setGameText("If opponent just initiated a battle at a system or sector where you have a starship with maneuver > 3, use 1 Force to add one battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.system_or_sector, Filters.canBeTargetedBy(self)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            Filter starshipFilter = Filters.and(Filters.and(Filters.your(self), Filters.starship, Filters.maneuverMoreThan(3), Filters.participatingInBattle));
            if (GameConditions.canTarget(game, self, starshipFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setActionMsg("Add one battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose starship", starshipFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard starshipTargeted) {
                                action.addAnimationGroup(starshipTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Add one battle destiny by targeting " + GameUtils.getCardLink(starshipTargeted),
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