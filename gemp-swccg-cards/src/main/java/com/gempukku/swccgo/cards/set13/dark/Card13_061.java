package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Come Here You Big Coward
 */
public class Card13_061 extends AbstractDefensiveShield {
    public Card13_061() {
        super(Side.DARK, Title.Come_Here_You_Big_Coward);
        setLore("'Chewie! Come here!'");
        setGameText("Plays on table. While you occupy a battleground and opponent occupies less than two battlegrounds, cancel: Asteroid Sanctuary, opponent's Force drains at non-battleground locations, and opponent's Force retrieval.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Asteroid_Sanctuary)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

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

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Asteroid_Sanctuary)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Asteroid_Sanctuary, Title.Asteroid_Sanctuary);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.non_battleground_location)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isForceRetrievalJustInitiated(game, effectResult, opponent)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force retrieval");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceRetrievalEffect(action));
            actions.add(action);
        }
        return actions;
    }
}