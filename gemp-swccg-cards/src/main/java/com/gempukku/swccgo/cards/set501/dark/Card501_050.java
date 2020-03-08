package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: Vader's Anger (V) (Errata)
 */
public class Card501_050 extends AbstractUsedInterrupt {
    public Card501_050() {
        super(Side.DARK, 5, "Vader's Anger", Uniqueness.UNIQUE);
        setLore("Anger and aggression fuel the dark side of the Force.");
        setGameText("For remainder of turn, opponent may not cancel your lightsaber weapon (or ‘choke’) destiny draws. OR If Vader in battle alone, your total battle destiny is +1 for each character in battle. OR Cancel It’s A Trap. OR Take Sith Fury into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
        setTestingText("Vader's Anger (V) (Errata)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_A_Trap)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_A_Trap, Title.Its_A_Trap);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isAllAbilityInBattleProvidedBy(game, playerId, Filters.Vader)) {
            final int count = Filters.countActive(game, self, Filters.and(Filters.character, Filters.participatingInBattle));
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
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

        GameTextActionId gameTextActionId = GameTextActionId.VADERS_ANGER__UPDLOAD_SITH_FURY;
        if(GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)){

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Sith_Fury), true)
            );
        }

        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action,
                        new MayNotCancelWeaponDestinyModifier(self, game.getOpponent(playerId), Filters.and(Filters.your(playerId), Filters.lightsaber)), "")
        );
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action,
                        new ModifyGameTextModifier(self, Filters.or(Filters.title(Title.Darth_Vader_Dark_Lord_of_the_Sith), Filters.title(Title.Physical_Choke)) ,ModifyGameTextType.CHOKE_DESTINY_CANNOT_BE_CANCELLED),"")
        );

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Its_A_Trap)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}