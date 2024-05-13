package com.gempukku.swccgo.cards.set101.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Interrupt
 * Subtype: Lost
 * Title: Run Luke, Run!
 */
public class Card101_003 extends AbstractLostInterrupt {
    public Card101_003() {
        super(Side.LIGHT, 6, Title.Run_Luke_Run, Uniqueness.UNIQUE, ExpansionSet.PREMIERE_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("After seeing Vader strike down Obi-Wan, Luke attacked recklessly until he heard the old Jedi Master's voice warn, 'Run Luke, Run!'");
        setGameText("At any time, move Luke as a regular move (for free) to a battle just initiated at an adjacent site. Luke is power +2 during that battle, unless Vader is present or adjacent to the battle site. OR You may cancel Vader's Obsession.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.Luke, Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, true, 0, null, Filters.battleLocation));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.adjacentSiteTo(self, filter))) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move Luke to battle");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(battleLocation);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(battleLocation),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveCardUsingLandspeedEffect(action, playerId, finalTarget, true, Filters.battleLocation));
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new PowerModifier(self, Filters.Luke, new CantSpotCondition(self, Filters.and(Filters.Vader, Filters.or(Filters.presentAt(Filters.battleLocation),
                                                                        Filters.at(Filters.adjacentSiteTo(self, Filters.battleLocation))))), 2), null));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Vaders_Obsession)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Vaders_Obsession, Title.Vaders_Obsession);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Vaders_Obsession)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}