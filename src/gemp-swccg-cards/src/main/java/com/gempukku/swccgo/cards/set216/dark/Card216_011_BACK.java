package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.CommencePrimaryIgnitionTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceLossInitiatedResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Objective 
 * Title: On The Verge Of Greatness / Taking Control Of The Weapon
 */
public class Card216_011_BACK extends AbstractObjective {
    public Card216_011_BACK() {
        super(Side.DARK, 7, Title.Taking_Control_Of_The_Weapon, ExpansionSet.SET_16, Rarity.V);
        setGameText("While this side up, your Force generation is +2 for each 'blown away' Scarif site. Tarkin Doctrine is immune to Alter and, when it initiates Force loss, may take any one card into hand from Force Pile. Once per turn, if opponent's character just lost from your site, may place it out of play unless opponent loses 1 Force. Tarkin adds 3 to total of Commence Primary Ignition. \n" +
                "Flip this card if you have no leaders on Scarif. \n" +
                "Place this card out of play if Shield Gate not on table or if Death Star has been 'blown away.'");
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new MultiplyEvaluator(2,
                new OnTableEvaluator(self, Filters.and(Filters.partOfSystem(Title.Scarif), Filters.blown_away))), playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Tarkin_Doctrine, Title.Alter));
        modifiers.add(new CommencePrimaryIgnitionTotalModifier(self, new OnTableCondition(self, Filters.Tarkin),3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        // when [Tarkin Doctrine] initiates Force loss, may take any one card into hand from Force Pile
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.forceLossInitiated(game, effectResult)
                && GameConditions.hasForcePile(game, playerId)) {
            PhysicalCard source = ((ForceLossInitiatedResult)effectResult).getSource();
            if (source != null
                    && Filters.Tarkin_Doctrine.accepts(game, source)) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Take a card into hand from Force Pile");
                action.setActionMsg("Take any one card into hand from Force Pile");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new TakeCardIntoHandFromForcePileEffect(action, playerId, true));

                actions.add(action);
            }
        }

        // Once per turn, may place opponent's character just lost from your site out of play unless opponent loses 1 Force.
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.and(Filters.your(self), Filters.site))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final GameState gameState = game.getGameState();
            final PhysicalCard lostCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(lostCard) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(lostCard) + " out of play");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new YesNoDecision("Do you want to lose 1 Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play?") {
                                @Override
                                protected void yes() {
                                    gameState.sendMessage(opponent + " chooses to lose 1 Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play");
                                    action.appendEffect(
                                            new LoseForceEffect(action, opponent, 1, true));
                                }
                                protected void no() {
                                    gameState.sendMessage(opponent + " chooses to not lose 1 Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play");
                                    action.appendEffect(
                                            new PlaceCardOutOfPlayFromOffTableEffect(action, lostCard));
                                }
                            }
                    ));
            actions.add(action);
        }


        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star, true)))
                || (TriggerConditions.isTableChanged(game, effectResult)
                && (!GameConditions.canSpot(game, self, Filters.Shield_Gate)
                || GameConditions.canSpot(game, self, Filters.and(Filters.blown_away, Filters.and(Filters.system, Filters.title(Title.Death_Star, true))))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(self), Filters.leader, Filters.on(Title.Scarif)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
