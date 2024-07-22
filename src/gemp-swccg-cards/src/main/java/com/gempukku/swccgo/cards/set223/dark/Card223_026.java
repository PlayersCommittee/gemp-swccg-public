package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardPlayedThisTurnByPlayerCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Defensive Shield
 * Title: We'll Let Fate-a Decide, Huh? (V)
 */
public class Card223_026 extends AbstractDefensiveShield {
    public Card223_026() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We'll Let Fate-a Decide, Huh?", ExpansionSet.SET_23, Rarity.V);
        setLore("Qui-Gon was not one to leave the success of an elaborate plan to chance.");
        setGameText("Plays on table. Cancels Beggar, Colo Claw Fish, Frozen Assets, Goo Nee Tay, and Revolution. Each player may only play one card with 'sabacc' in title each turn. You may lose 1 Force to cancel a card with 'sabacc' in title.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_DEFENSIVE_SHIELD);
        setVirtualSuffix(true);
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Beggar, Filters.Colo_Claw_Fish, Filters.Frozen_Assets, Filters.Goo_Nee_Tay, Filters.Revolution))
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
            if (GameConditions.canTargetToCancel(game, self, Filters.Beggar)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Beggar, Title.Beggar);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Colo_Claw_Fish)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Colo_Claw_Fish, Title.Colo_Claw_Fish);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Frozen_Assets)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Frozen_Assets, Title.Frozen_Assets);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Goo_Nee_Tay)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Goo_Nee_Tay, Title.Goo_Nee_Tay);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Revolution, Title.Revolution);
                actions.add(action);
            }
       }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter cardWithSabaccInTitle = Filters.titleContains("sabacc");

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, cardWithSabaccInTitle, new CardPlayedThisTurnByPlayerCondition(playerId, cardWithSabaccInTitle), playerId));
        modifiers.add(new MayNotPlayModifier(self, cardWithSabaccInTitle, new CardPlayedThisTurnByPlayerCondition(opponent, cardWithSabaccInTitle), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(playerId), Filters.titleContains("sabacc")))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.opponents(playerId), Filters.titleContains("sabacc"));

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, "opponent's card with 'sabacc' in title");
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
