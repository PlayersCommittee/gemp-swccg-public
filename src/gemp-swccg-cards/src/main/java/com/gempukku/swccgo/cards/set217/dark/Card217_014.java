package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 17
 * Type: Effect
 * Title: Blast Door Controls (V)
 */

public class Card217_014 extends AbstractNormalEffect {
    public Card217_014() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Blast_Door_Controls, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLore("Panels control blast doors and key security lock-downs during alerts. Luke destroyed one, locking Imperial forces out of Hangar Bay 327.");
        setGameText("Deploy on table. Cancels Blast The Door, Kid!, Narrow Escape, and Rebel Barrier. If opponent just canceled a battle (or just moved a character, starship, or vehicle away from a battle), opponent loses 1 Force.");
        addIcons(Icon.VIRTUAL_SET_24);
        setVirtualSuffix(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // (Copied from Blast Door Controls)
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Blast_The_Door_Kid, Filters.Narrow_Escape, Filters.Rebel_Barrier))
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
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // (Copied from Blast Door Controls)
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Blast_The_Door_Kid)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Blast_The_Door_Kid, Title.Blast_The_Door_Kid);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Narrow_Escape)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Narrow_Escape, Title.Narrow_Escape);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Barrier)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Barrier, Title.Rebel_Barrier);
                actions.add(action);
            }
        }

        // (Copied from Close The Blast Doors!)
        Filter filter = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle));
        if (TriggerConditions.battleCanceledAt(game, effectResult, game.getOpponent(self.getOwner()), Filters.any)
                || (GameConditions.isDuringBattle(game)
                && TriggerConditions.moved(game, effectResult, game.getOpponent(self.getOwner()), filter)
                && TriggerConditions.movedFromLocation(game, effectResult, filter, game.getGameState().getBattleLocation()))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));

            actions.add(action);
        }

        return actions;
    }
}
