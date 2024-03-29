package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearTargetedCardsEffect;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Subtype: Immediate
 * Title: I Never Ask That Question
 */
public class Card210_033 extends AbstractImmediateEffect {
    public Card210_033() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I Never Ask That Question", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("");
        setGameText("If opponent just played an Interrupt, use 2 Force to deploy on table and stack that card here. If opponent just drew destiny (or played an Interrupt) with the same card title as card here, they lose 1 Force. Immune to Control.");
        addIcons(Icon.GRABBER, Icon.EPISODE_VII, Icon.VIRTUAL_SET_10);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(self.getOwner());

        // If opponent just played an Interrupt with the same card title as card here, they lose 1 Force.

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.sameTitleAsStackedOn(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 force");
            action.setActionMsg("Make opponent lose 1 force");

            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));

            actions.add(action);
        }
        return actions;

    }


    @Override
    protected List<PlayCardAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // If opponent just played an Interrupt, use 2 Force to deploy on table and stack that card here. (part 1)

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
                if (action != null) {
                    action.setText("Deploy to 'grab' " + GameUtils.getFullName(cardBeingPlayed));
                    // Target the card to grab
                    action.appendTargeting(
                            new SetTargetedCardEffect(action, self, TargetId.IMMEDIATE_EFFECT_TARGET_1, null, cardBeingPlayed, Filters.samePermanentCardId(cardBeingPlayed)));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }



    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // If opponent just played an Interrupt, use 2 Force to deploy on table and stack that card here. (part 2)

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard cardToGrab = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
            if (GameConditions.canBeGrabbed(game, self, cardToGrab)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(self.getOwner());
                action.setText("'Grab' " + GameUtils.getFullName(cardToGrab));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardToGrab));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardToGrab, self));
                action.appendEffect(
                        new ClearTargetedCardsEffect(action, self));

                actions.add(action);
            }
        }


        // If opponent just drew destiny with the same card title as card here, they lose 1 Force.

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            final PhysicalCard cardDrawn = destinyDrawnResult.getCard();
            if ((cardDrawn != null)
                    && Filters.sameTitleAsStackedOn(self).accepts(game, cardDrawn)) {

                final String cardOwner = cardDrawn.getOwner();
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose 1 force");
                action.setActionMsg("Make opponent lose 1 force");

                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, cardOwner, 1));

                actions.add(action);
            }
        }

        return actions;
    }

}