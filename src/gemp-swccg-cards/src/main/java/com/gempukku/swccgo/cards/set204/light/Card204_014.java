package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainCompletedResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 4
 * Type: Effect
 * Title: Graveyard Of Giants
 */
public class Card204_014 extends AbstractNormalEffect {
    public Card204_014() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Graveyard_Of_Giants, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("High-speed collisions are a constant danger during chaotic starfighter dogfights. Scanners can be jammed. Pilots rely on vision, increasing the chances of such accidents.");
        setGameText("Deploy on table. Starships and vehicles just lost from Jakku locations are stacked here face down. Unless no cards here, if a player just Force drained on Jakku: that player may retrieve 1 Force (if they Force drained with a scavenger, may randomly retrieve instead), peek at cards stacked here, and then place one in owner's Lost Pile. [Immune to Alter]");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.or(Filters.starship, Filters.vehicle), Filters.Jakku_location)) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack " + GameUtils.getFullName(cardLost));
            action.setActionMsg("Stack " + GameUtils.getCardLink(cardLost) + " on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, true, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getGraveyardOfGiantsOptionalActions(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getGraveyardOfGiantsOptionalActions(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    private List<OptionalGameTextTriggerAction> getGraveyardOfGiantsOptionalActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.on(Title.Jakku))
                && GameConditions.hasStackedCards(game, self)) {
            PhysicalCard forceDrainLocation = ((ForceDrainCompletedResult) effectResult).getLocation();
            boolean forceDrainedWithScavenger = GameConditions.canSpot(game, self, Filters.and(Filters.owner(playerId), Filters.scavenger, Filters.at(forceDrainLocation)));

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force and peek at stacked cards");
            action.setActionMsg("Retrieve 1 Force and then place a card stacked here in owner's Lost Pile");
            // Perform result(s)
            if (forceDrainedWithScavenger) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Do you want to randomly retrieve instead?") {
                                    @Override
                                    protected void yes() {
                                        action.insertEffect(
                                                new RetrieveForceEffect(action, playerId, 1, true));
                                    }

                                    @Override
                                    protected void no() {
                                        action.insertEffect(
                                                new RetrieveForceEffect(action, playerId, 1));
                                    }
                                }
                        ));
            }
            else {
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1));
            }
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, self) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, playerId, selectedCard, true));
                        }
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose card" + GameUtils.s(numCardsToChoose) + " to place in owner's Lost Pile";
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}