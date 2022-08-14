package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 3
 * Type: Effect
 * Title: Thrown Back (V)
 */
public class Card203_016 extends AbstractNormalEffect {
    public Card203_016() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Thrown Back", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Being sent to dispose of two Jedi is the battle droid equivalent of a really bad day at the office.");
        setGameText("Deploy on table. While no card stacked here, may stack opponent's just-played Interrupt here. Opponent may use 3 Force to place an Interrupt here in Used Pile. Once per turn, unless your protocol droid on table, may place a card from hand on bottom of Used Pile. [Immune to Alter.]");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.GRABBER, Icon.VIRTUAL_SET_3);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)
                && !GameConditions.hasStackedCards(game, self)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.hasStackedCards(game, self, Filters.Interrupt)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place stacked Interrupt in Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, Filters.Interrupt) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose Interrupt";
                        }
                        @Override
                        protected void cardSelected(final PhysicalCard interrupt) {
                            action.setActionMsg("Place " + GameUtils.getCardLink(interrupt) + " in Used Pile");
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Perform result(s)
                            action.appendEffect(
                                    new PutStackedCardInUsedPileEffect(action, playerId, interrupt, false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && !GameConditions.canTarget(game, self, Filters.and(Filters.your(self), Filters.protocol_droid))
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand on bottom of Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromHandOnBottomOfUsedPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}