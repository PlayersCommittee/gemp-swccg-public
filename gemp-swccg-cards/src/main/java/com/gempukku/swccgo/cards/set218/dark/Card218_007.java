package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used
 * Title: A Dark Time For The Rebellion & Tarkin's Orders
 */
public class Card218_007 extends AbstractUsedInterrupt {
    public Card218_007() {
        super(Side.DARK, 5, "A Dark Time For The Rebellion & Tarkin's Orders", Uniqueness.UNIQUE);
        addComboCardTitles("A Dark Time For The Rebellion", "Tarkin's Orders");
        setGameText("For remainder of turn, opponent may not cancel your battle destiny draws or character weapon destiny draws. OR If you have two battlegrounds on table (and opponent does not), for remainder of turn, your total power in battles is +1 for each opponent's non-battleground location on table. OR Cancel It Could Be Worse or Nabrun Leids. OR If opponent does not occupy a battleground site (or if Menace Fades on table), cancel Projection Of A Skywalker.");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        List<PlayInterruptAction> actions = new LinkedList<>();

        final PlayInterruptAction protectBattleAndWeaponDestinyDrawsAction = new PlayInterruptAction(game, self);
        protectBattleAndWeaponDestinyDrawsAction.setText("Affect battle and weapon destiny draws");

        // Allow response(s)
        protectBattleAndWeaponDestinyDrawsAction.allowResponses("Prevent opponent from canceling your battle and character weapon destiny draws for remainder of turn",
                new RespondablePlayCardEffect(protectBattleAndWeaponDestinyDrawsAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        protectBattleAndWeaponDestinyDrawsAction.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(protectBattleAndWeaponDestinyDrawsAction,
                                        new MayNotCancelBattleDestinyModifier(self, playerId, opponent),
                                        "Prevents "+opponent+" from canceling "+playerId+"'s battle destiny draws")
                        );
                        protectBattleAndWeaponDestinyDrawsAction.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(protectBattleAndWeaponDestinyDrawsAction,
                                        new MayNotCancelWeaponDestinyModifier(self, opponent, Filters.and(Filters.your(self), Filters.character_weapon)),
                                        "Prevents "+opponent+" from canceling "+playerId+"'s character weapon destiny draws")
                        );
                    }
                }
        );
        actions.add(protectBattleAndWeaponDestinyDrawsAction);

        int yourBattlegroundCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.your(self), Filters.battleground));
        int opponentBattlegroundCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.opponents(self), Filters.battleground));

        if (yourBattlegroundCount >= 2
                && opponentBattlegroundCount < 2) {
            final int opponentNonBattlegroundCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.opponents(self), Filters.non_battleground_location));

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add " + opponentNonBattlegroundCount + " to total power");

            action.allowResponses("Add " + opponentNonBattlegroundCount + " to total power during battles for remainder of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                    new TotalPowerModifier(self, Filters.battleLocation, opponentNonBattlegroundCount, playerId),
                                    "Adds " + opponentNonBattlegroundCount + " to total power during battles"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.It_Could_Be_Worse)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.It_Could_Be_Worse, Title.It_Could_Be_Worse);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Nabrun_Leids)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Nabrun_Leids, Title.Nabrun_Leids);
            actions.add(action);
        }


        if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Projection_Of_A_Skywalker))
                && (!GameConditions.occupies(game, opponent, Filters.battleground_site)
                || GameConditions.canSpot(game, self, Filters.title(Title.Menace_Fades)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Projection_Of_A_Skywalker), Title.Projection_Of_A_Skywalker);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if ((TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.It_Could_Be_Worse, Filters.Nabrun_Leids))
                || (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Projection_Of_A_Skywalker))
                && (!GameConditions.occupies(game, game.getOpponent(playerId), Filters.battleground_site)
                || GameConditions.canSpot(game, self, Filters.title(Title.Menace_Fades)))))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }
}
