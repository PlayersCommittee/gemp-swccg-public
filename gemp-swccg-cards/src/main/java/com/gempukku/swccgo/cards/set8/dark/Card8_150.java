package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Outflank
 */
public class Card8_150 extends AbstractUsedInterrupt {
    public Card8_150() {
        super(Side.DARK, 6, "Outflank", Uniqueness.UNIQUE);
        setLore("Commander Igar's defense of Endor called for the use of speeder bikes to harass any attacking Rebels.");
        setGameText("During a battle, if you have weapons at both sites adjacent to that battle, add 3 to your total power and add one battle destiny. OR For remainder of turn, your biker scout at an exterior site is power +1 and adds 1 to each of that character's weapon destiny draws.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            PhysicalCard battleSite = game.getGameState().getBattleLocation();
            int numAdjacentWithWeapons = Filters.countTopLocationsOnTable(game, Filters.and(Filters.adjacentSite(battleSite),
                    Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.weapon))));
            if (numAdjacentWithWeapons >= 2) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add 3 to total power and add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 3, playerId, "Adds 3 to total power"));
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter filter = Filters.and(Filters.your(self), Filters.biker_scout, Filters.at(Filters.exterior_site));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add to biker scout's power and weapon destiny draws");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose biker scout", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Add 1 to " + GameUtils.getCardLink(targetedCard) + "'s power and 1 to each of " + GameUtils.getCardLink(targetedCard) + "'s weapon destiny draws",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, 1));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new EachWeaponDestinyModifier(self, Filters.any, finalTarget, 1),
                                                            "Adds 1 to each of " + GameUtils.getCardLink(targetedCard) + "'s weapon destiny draws"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}