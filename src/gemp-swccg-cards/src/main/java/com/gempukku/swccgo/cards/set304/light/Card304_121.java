package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
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
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used
 * Title: That Got Him! & We've Got This
 */
public class Card304_121 extends AbstractUsedInterrupt {
    public Card304_121() {
        super(Side.LIGHT, 5, "That Got Him! & We've Got This", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        addComboCardTitles("That Got Him!", "We've Got This");
        setGameText("");
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
        if (GameConditions.canTargetToCancel(game, self, Filters.Do_A_Backflip_Derrin)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Do_A_Backflip_Derrin, Title.Do_A_Backflip_Derrin);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Elis_Helrot)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Elis_Helrot, Title.Elis_Helrot);
            actions.add(action);
        }


        if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Projection_Of_A_Skywalker))
                && (!GameConditions.occupies(game, opponent, Filters.battleground_site)
                || GameConditions.canSpot(game, self, Filters.titleContains("A Bright Center To The Universe")))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.After_Her), Title.After_Her);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if ((TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Do_A_Backflip_Derrin, Filters.Elis_Helrot))
                || (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.After_Her))
                && (!GameConditions.occupies(game, game.getOpponent(playerId), Filters.battleground_site)
                || GameConditions.canSpot(game, self, Filters.titleContains("A Bright Center To The Universe")))))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }
}
