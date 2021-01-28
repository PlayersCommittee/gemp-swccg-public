package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block D
 * Type: Effect
 * Title: A Useless Gesture (V)
 */
public class Card601_031 extends AbstractDefensiveShield {
    public Card601_031() {
        super(Side.DARK, Title.A_Useless_Gesture);
        setVirtualSuffix(true);
        setLore("Imperial officers aboard the Death Star considered the Rebellion a minor threat.");
        setGameText("Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here (if possible) and use +1 Force for each card here, even if Interrupt is normally free. Revolution is canceled.");
        addIcons(Icon.REFLECTIONS_III, Icon.GRABBER, Icon.BLOCK_D);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.Interrupt, Filters.inLostPile(opponent),
                Filters.not(Filters.sameTitleAsStackedOn(self, Filters.and(Filters.grabber, Filters.not(self))))),
                new AddEvaluator(new StackedEvaluator(self), 1)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isPlayingCardFromLostPile(game, effect, opponent, Filters.and(Filters.Interrupt,
                Filters.not(Filters.sameTitleAsStackedOn(self, Filters.and(Filters.grabber, Filters.not(self))))))) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Revolution)
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
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Revolution, Title.Revolution);
            actions.add(action);
        }
        return actions;
    }
}