package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Rescue In The Clouds (V)
 */
public class Card501_031 extends AbstractUsedOrLostInterrupt {
    public Card501_031() {
        super(Side.LIGHT, 5, "Rescue In The Clouds", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'I know where Luke is.'");
        setGameText("USED: At a system or Bespin location, choose: Cancel a just drawn destiny targeting the ability or defense value of your non-Undercover character of ability < 5. OR During battle, draw one battle destiny if unable to otherwise. LOST: Cancel Close Call.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_9);
        setTestingText("Rescue in the Clouds (V) (ERRATA)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Close_Call)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Close_Call, Title.Close_Call);
            actions.add(action);
        }

        Filter systemOrBespinLocation = Filters.or(Filters.system, Filters.Bespin_location);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, systemOrBespinLocation)) {

            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            if (battleLocation != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Draw battle destiny if unable to otherwise");
                action.addAnimationGroup(battleLocation);
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.at(battleLocation), 1), "Draws battle destiny if unable to otherwise")
                                );

                            }
                        }
                );

                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult,
                Filters.and(Filters.your(self), Filters.not(Filters.undercover_spy), Filters.character, Filters.abilityLessThan(5),
                        Filters.at(Filters.or(Filters.system, Filters.Bespin_location))))
                && GameConditions.canCancelDestiny(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Close_Call)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

}
