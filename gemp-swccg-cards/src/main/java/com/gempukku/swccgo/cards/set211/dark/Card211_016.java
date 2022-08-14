package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: Vader's Anger (V)
 */
public class Card211_016 extends AbstractUsedInterrupt {
    public Card211_016() {
        super(Side.DARK, 5, "Vader's Anger", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Anger and aggression fuel the dark side of the Force.");
        setGameText("Vader follows (using landspeed for free) an opponent's character that just moved from same site. OR If Vader in battle alone, your total battle destiny is +1 for each character in battle. OR If Vader in battle, cancel Dodge, It's A Trap!, or Obi-Wan's Journal.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.sameLocationAs(self, Filters.Vader))
                && GameConditions.canTarget(game, self, Filters.Vader)) {

            MovedResult movedResult = (MovedResult) effectResult;
            final PhysicalCard toLocation = movedResult.getMovedTo();

            final PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, true, 0, null, Filters.sameLocation(toLocation))));

            if (vader != null) {
                PhysicalCard cardToFollow = movedResult.getMovedCards().iterator().next();
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Follow " + GameUtils.getFullName(cardToFollow));
                action.setActionMsg("Follow " + GameUtils.getCardLink(cardToFollow) + " for free using landspeed");

                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new MoveCardUsingLandspeedEffect(action, playerId, vader, true, toLocation));
                    }
                });

                return Collections.singletonList(action);
            }

        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_A_Trap)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_A_Trap, Title.Its_A_Trap);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dodge)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dodge, Title.Dodge);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.ObiWans_Journal)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.ObiWans_Journal, Title.ObiWans_Journal);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Vader, Filters.alone))) {
            final int count = Filters.countActive(game, self, Filters.and(Filters.character, Filters.participatingInBattle));
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + count + " to total battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, count));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Its_A_Trap, Filters.Dodge, Filters.ObiWans_Journal))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}