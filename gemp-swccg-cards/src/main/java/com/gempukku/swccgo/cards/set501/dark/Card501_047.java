package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Fanfare (V) (Errata)
 */
public class Card501_047 extends AbstractDefensiveShield {
    public Card501_047() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Fanfare");
        setVirtualSuffix(true);
        setLore("The Boonta Eve crowds are always looking for new and exciting developments at every Podrace.");
        setGameText("Plays on table. Once per game, may take an Immediate Effect into hand from Reserve Deck; reshuffle. While opponent occupies no battleground systems, Staging Areas is suspended. Ice Storm, Lost In The Wilderness, Order To Engage, Sandwhirl and Scramble are canceled.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
        setTestingText("Fanfare (V) (Errata)");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Order_To_Engage, Filters.Scramble, Filters.Ice_Storm, Filters.Sandwhirl, Filters.Lost_In_The_Wilderness))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Order_To_Engage)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Order_To_Engage, Title.Order_To_Engage);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Scramble)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scramble, Title.Scramble);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Ice_Storm)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Ice_Storm, Title.Ice_Storm);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Sandwhirl)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sandwhirl, Title.Sandwhirl);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Lost_In_The_Wilderness)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lost_In_The_Wilderness, Title.Lost_In_The_Wilderness);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FANFARE__UPLOAD_IMMEDIATE_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Immediate Effect into hand from Reserve Deck");
            action.setActionMsg("Take an Immediate Effect into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Immediate_Effect, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Staging_Areas, new NotCondition(new OccupiesCondition(opponent, Filters.battleground_system))));
        return modifiers;
    }
}