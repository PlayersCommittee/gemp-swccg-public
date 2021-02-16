package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Epic Event
 * Title: Communing
 */
public class Card601_041 extends AbstractEpicEventDeployable {
    public Card601_041() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Communing, Uniqueness.UNIQUE);
        setGameText("If your starting location has exactly 2 [Light Side Force], play instead of a starting interrupt. deploy from Reserve Deck two always [Immune to Alter] Effects; reshuffle. Deploy on table; take into hand and stack a Jedi here from Reserve Deck (that Jedi is 'communing'); reshuffle. \n" +
                "Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone Is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.BLOCK_7);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new StackedEvaluator(self, self), self.getOwner()));
        modifiers.add(new CommuningModifier(self, Filters.and(Filters.stackedOn(self), Filters.Jedi)));
        modifiers.add(new ConsideredOutOfPlayModifier(self, Filters.stackedOn(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.hasStackedCards(game, self, 2)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Look at top cards and replace");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect(action, playerId));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();


        // Check condition(s)
        if(TriggerConditions.justLost(game,effectResult,Filters.Jedi)
                ||TriggerConditions.justPlacedOutOfPlayFromTable(game,effectResult,Filters.Jedi)) {

            final PhysicalCard jedi;
            if (TriggerConditions.justLost(game, effectResult, Filters.Jedi)) {
                LostFromTableResult result = (LostFromTableResult) effectResult;
                jedi = result.getCard();
            } else {
                PlacedCardOutOfPlayFromTableResult result = (PlacedCardOutOfPlayFromTableResult) effectResult;
                jedi = result.getCard();
            }

            if (jedi != null) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(jedi) + " here");
                action.setActionMsg("Stack " + GameUtils.getCardLink(jedi) + " on " + GameUtils.getCardLink(self));
                // Perform result(s)
                if (TriggerConditions.justLost(game, effectResult, Filters.Jedi)) {
                    action.appendEffect(new StackOneCardFromLostPileEffect(action, jedi, self, false, false, true));
                } else {
                    //TODO probably need a new StackCardFromOutOfPlayEffect
                }
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.title("I Can't Believe He's Gone"))) {


            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("I Can't Believe He's Gone"), "I Can't Believe He's Gone");
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title("I Can't Believe He's Gone"))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    public boolean playableAsStartingInterrupt(SwccgGame game, PhysicalCard self) {
        return true;
    }

    @Override
    public PlayCardAction getStartingInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && Filters.iconCount(Icon.LIGHT_FORCE, 2).accepts(game, startingLocation)) {
            final PlayEpicEventAction action = new PlayEpicEventAction(self);
            action.setEpicEventState(new EpicEventState(self, EpicEventState.Type.PLAY_INSTEAD_OF_STARTING_INTERRUPT) {
                @Override
                public PhysicalCard getEpicEvent() {
                    return super.getEpicEvent();
                }
            });

            action.setText("Deploy Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to two Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 2, true, false));
                            action.appendEffect(new DeploySingleCardEffect(action, self, Zone.SIDE_OF_TABLE, false, null, false));
                            action.appendEffect(
                                    new StackCardFromReserveDeckEffect(action, self, Filters.Jedi, true));

                        }
                    }
            );
            return action;

        }

        return null;
    }
}