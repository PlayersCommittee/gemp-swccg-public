package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Who's Scruffy-Looking?
 */
public class Card3_052 extends AbstractUsedInterrupt {
    public Card3_052() {
        super(Side.LIGHT, 6, "Who's Scruffy-Looking?", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R1);
        setLore("'I must've hit pretty close to the mark to get her all riled up like that, huh kid?'");
        setGameText("Cancel Scruffy-Looking Nerf Herder or I'd Just As Soon Kiss A Wookiee or Furry Fury or This Is Just Wrong or Death Mark or Mournful Roar or Takeel or Ket Maliss. OR Double Rennek's power for remainder of turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.or(Filters.Scruffy_Looking_Nerf_Herder, Filters.Id_Just_As_Soon_Kiss_A_Wookiee, Filters.Furry_Fury,
                Filters.This_Is_Just_Wrong, Filters.Death_Mark, Filters.Mournful_Roar, Filters.Takeel, Filters.Ket_Maliss);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Scruffy_Looking_Nerf_Herder)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scruffy_Looking_Nerf_Herder, Title.Scruffy_Looking_Nerf_Herder);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Id_Just_As_Soon_Kiss_A_Wookiee)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Id_Just_As_Soon_Kiss_A_Wookiee, Title.Id_Just_As_Soon_Kiss_A_Wookiee);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Furry_Fury)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Furry_Fury, Title.Furry_Fury);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.This_Is_Just_Wrong)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.This_Is_Just_Wrong, Title.This_Is_Just_Wrong);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Death_Mark)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Death_Mark, Title.Death_Mark);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Mournful_Roar)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Mournful_Roar, Title.Mournful_Roar);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Takeel)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Takeel, Title.Takeel);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Ket_Maliss)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Ket_Maliss, Title.Ket_Maliss);
            actions.add(action);
        }

        final Filter rennekFilter = Filters.Rennek;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, rennekFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Double Rennek's power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Rennek", rennekFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard rennek) {
                            action.addAnimationGroup(rennek);
                            // Allow response(s)
                            action.allowResponses("Double " + GameUtils.getCardLink(rennek) + "'s power",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            float curPower = game.getModifiersQuerying().getPower(game.getGameState(), rennek);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new PowerModifier(self, rennek, curPower),
                                                            "Doubles " + GameUtils.getCardLink(rennek) + "'s power"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}