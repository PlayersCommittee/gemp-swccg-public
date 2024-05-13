package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Epic Event
 * Title: His Destiny
 */
public class Card221_066 extends AbstractEpicEventDeployable {
    public Card221_066() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "His Destiny", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("If He Is The Chosen One on table, deploy on table. " +
                "Opponent's total Force generation is -1 (and your total power everywhere is +1) for each [Endor] or [Death Star II] battleground you occupy (limit 2). " +
                "Once per turn, if a card was just stacked on I Feel The Conflict, may peek at top two cards of your Reserve Deck; take one into hand and shuffle your Reserve Deck." +
                "May lose 2 Force to cancel You Are Beaten or an attempt to steal Luke's Lightsaber. " +
                "During your move phase, may flip a card stacked on I Feel The Conflict face up to relocate Luke between a battleground site and your Death Star II site.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.He_Is_The_Chosen_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Evaluator battlegroundCount = new MaxLimitEvaluator(new OccupiesEvaluator(playerId, Filters.and(Filters.or(Icon.ENDOR, Icon.DEATH_STAR_II), Filters.battleground)), 2);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new TotalForceGenerationModifier(self, new NegativeEvaluator(battlegroundCount), opponent));
        modifiers.add(new TotalPowerModifier(self, Filters.location, battlegroundCount, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();

        if (GameConditions.canTargetToCancel(game, self, Filters.You_Are_Beaten)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.You_Are_Beaten, Title.You_Are_Beaten);
            action.appendCost(new LoseForceEffect(action, playerId, 2, true));
            actions.add(action);
        }

        // Relocate Luke
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        final Filter battlegroundFilter = Filters.battleground_site;
        final Filter dsiisiteFilter = Filters.and(Filters.your(self), Filters.Death_Star_II_site);
        final Filter lukeFilter = Filters.Luke;


        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && !Filters.filterStacked(game, Filters.and(Filters.stackedOn(self, Filters.I_Feel_The_Conflict), Filters.face_down)).isEmpty()) {

            if (GameConditions.canTarget(game, self, Filters.and(lukeFilter, Filters.at(battlegroundFilter), Filters.canBeRelocatedToLocation(dsiisiteFilter, 0)))) {
                actions.add(getRelocationAction(playerId, game, self, gameTextSourceCardId, gameTextActionId, "Relocate Luke to a Death Star II site", battlegroundFilter, dsiisiteFilter, lukeFilter));
            }

            if (GameConditions.canTarget(game, self, Filters.and(lukeFilter, Filters.at(dsiisiteFilter), Filters.canBeRelocatedToLocation(battlegroundFilter, 0)))) {
                actions.add(getRelocationAction(playerId, game, self, gameTextSourceCardId, gameTextActionId, "Relocate Luke to a battleground site", dsiisiteFilter, battlegroundFilter, lukeFilter));
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.You_Are_Beaten)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceEffect(action, playerId, 2, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if(TriggerConditions.justStackedCardOn(game, effectResult, Filters.any, Filters.I_Feel_The_Conflict)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top two cards of Reserve Deck");
            action.setActionMsg("Peek at top two cards of Reserve Deck, take one into hand, and shuffle your Reserve Deck");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            boolean twoCardsInReserve = GameConditions.numCardsInReserveDeck(game, playerId) >= 2;
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));
            if (twoCardsInReserve) {
                action.appendEffect(
                        new ShuffleReserveDeckEffect(action, playerId));
            }
            actions.add(action);
        }

        if (TriggerConditions.isAboutToBeStolen(game, effectResult, Filters.Lukes_Lightsaber)) {
            final AboutToBeStolenResult aboutToStealCardResult = (AboutToBeStolenResult) effectResult;
            final PhysicalCard weaponToBeStolen = aboutToStealCardResult.getCardToBeStolen();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel attempt");
            action.setActionMsg("Cancel attempt to steal "+ GameUtils.getCardLink(weaponToBeStolen));

            action.appendCost(
                    new LoseForceEffect(action, playerId, 2, true));
            action.appendEffect(
                    new PreventEffectOnCardEffect(action, aboutToStealCardResult.getPreventableCardEffect(), weaponToBeStolen, null));
            actions.add(action);
        }
        return actions;
    }

    private TopLevelGameTextAction getRelocationAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, String actionText, final Filter currentLocationFilter, final Filter relocateLocationFilter, final Filter characterFilter) {
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
        action.setText(actionText);

        action.appendUsage(
                new OncePerPhaseEffect(action));
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.and(characterFilter, Filters.at(currentLocationFilter), Filters.canBeRelocatedToLocation(relocateLocationFilter, 0))) {
                    @Override
                    protected void cardTargeted(final int targetGroupIdCharacter, final PhysicalCard characterToRelocate) {
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterToRelocate) + " to",
                                        Filters.and(relocateLocationFilter, Filters.locationCanBeRelocatedTo(characterToRelocate, 0))) {
                                    @Override
                                    protected void cardTargeted(final int targetGroupIdSite, final PhysicalCard siteSelected) {
                                        action.appendTargeting(new ChooseStackedCardEffect(action, playerId, Filters.I_Feel_The_Conflict, Filters.face_down, true) {
                                            @Override
                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                action.addAnimationGroup(characterToRelocate);
                                                action.addAnimationGroup(siteSelected);

                                                // Pay cost(s)
                                                action.appendCost(
                                                        new FlipSingleSidedStackedCard(action, selectedCard));
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, siteSelected, 0));
                                                action.allowResponses(new RespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupIdSite);
                                                        PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupIdCharacter);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RelocateBetweenLocationsEffect(action, finalCharacter, finalSite));
                                                    }
                                                });
                                            }
                                        });
                                    }

                                }
                        );
                    }
                }
        );
        return action;
    }
}