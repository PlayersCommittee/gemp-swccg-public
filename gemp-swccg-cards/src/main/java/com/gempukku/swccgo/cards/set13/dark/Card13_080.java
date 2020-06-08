package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.choose.StackDestinyCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: Opee Sea Killer
 */
public class Card13_080 extends AbstractNormalEffect {
    public Card13_080() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Opee_Sea_Killer, Uniqueness.UNIQUE);
        setLore("With a vicious array of pointed teeth, the opee sea killer is a fearsome hunter. When outmatched in size, it uses small crevices to avoid bigger predators.");
        setGameText("Deploy on table. Cancels Sando Aqua Monster. While no card here, may place an opponent's just-drawn battle destiny face-up here. If opponent just drew battle destiny with the same card title as card here, place both cards in opponent's Lost Pile. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Sando_Aqua_Monster)
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
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Sando_Aqua_Monster)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sando_Aqua_Monster, Title.Sando_Aqua_Monster);
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.hasStackedCards(game, self)) {
            PhysicalCard stackedCard = game.getGameState().getStackedCards(self).get(0);
            if (GameConditions.isDestinyCardMatchTo(game, Filters.sameTitleAs(stackedCard))
                    && GameConditions.canMakeDestinyCardLost(game)) {
                PhysicalCard destinyCard = game.getGameState().getTopDrawDestinyState().getDrawDestinyEffect().getDrawnDestinyCard();
                List<PhysicalCard> cardsToLose = Arrays.asList(stackedCard, destinyCard);

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place cards in Lost Pile");
                action.setActionMsg("Place " + GameUtils.getAppendedNames(cardsToLose) + " in Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromOffTableSimultaneouslyEffect(action, cardsToLose, playerId, false));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && !GameConditions.hasStackedCards(game, self)
                && GameConditions.canStackDestinyCard(game)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack battle destiny here");
            action.setActionMsg("Stack just-drawn battle destiny on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new StackDestinyCardEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }
}