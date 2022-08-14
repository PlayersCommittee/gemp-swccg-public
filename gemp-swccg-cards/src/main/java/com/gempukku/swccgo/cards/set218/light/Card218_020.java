package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Character
 * Subtype: Resistance
 * Title: Fleet Admiral Gial Ackbar
 */
public class Card218_020 extends AbstractResistance {
    public Card218_020() {
        super(Side.LIGHT, 1, 2, 2, 3, 6, "Fleet Admiral Gial Ackbar", Uniqueness.UNIQUE);
        setLore("Mon Calamari leader.");
        setGameText("Your starships here are power +1. If opponent just initiated battle here with a [First Order] starship, may place Ackbar out of play to cancel that battle. Cancels Lateral Damage (or Overwhelmed) targeting a starship at same system.");
        addIcons(Icon.PILOT, Icon.EPISODE_VII, Icon.VIRTUAL_SET_18);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        addPersona(Persona.ACKBAR);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.starship, Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Icon.FIRST_ORDER, Filters.starship))
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.Ackbar)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Cancel battle");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Ackbar to place out of play", Filters.Ackbar) {
                                       @Override
                                       protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                           action.appendCost(
                                                   new PlaceCardOutOfPlayFromTableEffect(action, targetedCard));
                                           action.allowResponses(new RespondableEffect(action) {
                                               @Override
                                               protected void performActionResults(Action targetingAction) {
                                                   action.appendEffect(
                                                           new CancelBattleEffect(action));
                                               }
                                           });
                                       }

                                       protected boolean getUseShortcut() {
                                           return true;
                                       }
                                   }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.Lateral_Damage, Filters.cardTargeting(self, Filters.and(Filters.starship, Filters.atSameSystem(self))));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, filter)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, Title.Lateral_Damage);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.or(Filters.Lateral_Damage, Filters.Overwhelmed), Filters.and(Filters.starship, Filters.atSameSystem(self)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
