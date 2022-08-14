package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfCardPileAndChooseCardsToPutOnBottomEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromTableResult;
import com.gempukku.swccgo.logic.timing.results.StackedCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Epic Event
 * Title: Communing
 */
public class Card216_024 extends AbstractEpicEventDeployable {
    public Card216_024() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Communing, Uniqueness.UNIQUE);
        setGameText("Deploy on table (only at start of game). You may not deploy Jedi with 'communing' in game text. " +
                "One With The Force: If a Jedi is about to be lost (or placed out of play) from table, may stack that card here. " +
                "The Living Force: Jedi stacked here are 'communing' and are considered out of play. Your total Force generation is +1 for each Jedi 'communing.' " +
                "The Cosmic Force: Once per turn, may peek at the top X cards of your Force Pile or Lost Pile, where X = the number of Jedi 'communing'; may move one of those cards to the bottom of that pile.");
        addIcons(Icon.VIRTUAL_SET_16, Icon.EPISODE_I);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.Jedi, Filters.or(Filters.gameTextContains("communing"), Filters.gameTextContains("communings"))), playerId));
        modifiers.add(new CommuningModifier(self, Filters.and(Filters.stackedOn(self), Filters.Jedi)));
        modifiers.add(new ConsideredOutOfPlayModifier(self, Filters.stackedOn(self)));
        modifiers.add(new TotalForceGenerationModifier(self, new StackedEvaluator(self, self, Filters.Jedi), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.hasStackedCards(game, self, Filters.Jedi)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
        ) {
            final int stackedCount = Filters.countStacked(game, Filters.and(Filters.stackedOn(self), Filters.Jedi));

            if (stackedCount > 0) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at top " + stackedCount + " card" + (stackedCount == 1 ? "" : "s") + " of  Force Pile or Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));

                // Perform result(s)
                action.appendEffect(new ChooseExistingCardPileEffect(action, playerId, playerId, Filters.or(Zone.FORCE_PILE, Zone.LOST_PILE)) {
                    @Override
                    protected void pileChosen(final SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                        action.appendEffect(
                                new PeekAtTopCardsOfCardPileAndChooseCardsToPutOnBottomEffect(action, cardPile, cardPileOwner, stackedCount, 0, 1));
                    }
                });
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, Filters.Jedi)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.Jedi)
                || TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, Filters.Jedi)) {

            final PhysicalCard jedi;
            if (TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, Filters.Jedi)) {
                AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
                jedi = result.getCardToBePlacedOutOfPlay();
            } else {
                AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                jedi = result.getCardAboutToLeaveTable();
            }

            if (jedi != null
                && game.getModifiersQuerying().canBeTargetedBy(game.getGameState(), jedi, self, Collections.singleton(TargetingReason.TO_BE_PLACED_OUT_OF_PLAY))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(jedi) + " here");
                action.setActionMsg("Stack " + GameUtils.getCardLink(jedi) + " on " + GameUtils.getCardLink(self));
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, Filters.Jedi)) {
                            AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
                            result.getPreventableCardEffect().preventEffectOnCard(jedi);
                        } else {
                            AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                            result.getPreventableCardEffect().preventEffectOnCard(jedi);
                        }
                        action.appendEffect(new StackCardFromTableEffect(action, jedi, self));
                        action.appendEffect(new RestoreCardToNormalEffect(action, jedi, false));
                    }
                });
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // track which Jedi was initially stacked
        if (!GameConditions.cardHasWhileInPlayDataSet(self)
                && TriggerConditions.justStackedCardOn(game, effectResult, Filters.Jedi, self)) {
            PhysicalCard stacked = ((StackedCardResult)effectResult).getCard();
            if (stacked != null) {
                self.setWhileInPlayData(new WhileInPlayData());
                String communer = stacked.getBlueprint().getTitle();
                if (communer.equals(Title.Master_QuiGon_Jinn_An_Old_Friend))
                    communer = "Qui-Gon";
                else if (communer.equals(Title.Master_Kenobi))
                    communer = "Obi-Wan";
                else if (communer.equals(Title.Master_Yoda))
                    communer = "Yoda";

                game.getModifiersQuerying().setExtraInformationForArchetypeLabel(self.getOwner(), communer);
            }
        }
        return null;
    }
}