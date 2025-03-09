package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfUsedPileAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.ShuffleUsedPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCoveredByHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Objective
 * Title: The Empire Knows We're Here  / Prepare For Ground Assault
 */
public class Card222_027_BACK extends AbstractObjective {
    public Card222_027_BACK() {
        super(Side.LIGHT, 7, "Prepare For Ground Assault", ExpansionSet.SET_22, Rarity.V);
        setGameText("While this side up, cancels Hoth Sentry, Sunsdown, and the game text of your Admiral's Orders and unique (•) characters (except gunners, pilots, and troopers) on table. " +
                "Cards 'hit' by your artillery or vehicle weapons may not be used to satisfy attrition. " +
                "If you just initiated battle, may peek at top X cards of your Used Pile, " +
                "where X = number of Hoth battlegrounds you occupy; take one into hand and shuffle your Used Pile." +
                "Flip this card if opponent does not occupy your Hoth location.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPIRE_KNOWS_WERE_HERE__DOWNLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location from Reserve Deck");
            action.setActionMsg("Deploy Echo Command Center or a marker site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Echo_Command_Center, Filters.marker_site), true));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<>();

        // Front Side Modifiers
        modifiers.add(new MayNotBeCoveredByHothEnergyShieldModifier(self, Filters.or(Filters.Second_Marker, Filters.Third_Marker)));
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Ice_Storm, Filters.system, Filters.and(Icon.SPECIAL_EDITION, Filters.Leia), Filters.and(Filters.character, Filters.abilityMoreThan(4))), self.getOwner()));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title("Echo Base Garrison"), Title.Alter));

        // Back Side Modifiers
        Filter characterFilter = Filters.and(Filters.onTable, Filters.unique, Filters.character, Filters.not(Filters.or(Filters.trooper, Filters.gunner, Filters.pilot)));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.your(playerId), Filters.or(Filters.Admirals_Order, characterFilter))));

        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.hasUsedPile(game, playerId)) {
            final int valueOfX = (int) game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X,
                    Filters.countTopLocationsOnTable(game, Filters.and(Filters.Hoth_location, Filters.battleground, Filters.occupies(playerId))));

            if (valueOfX > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Peek at top " + valueOfX + " card" + GameUtils.s(valueOfX) + " of Used Pile");
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (valueOfX > game.getGameState().getUsedPile(playerId).size()) {
                            // if they don't have enough cards in Used Pile just peek at the top and cancel the rest of the action
                            action.appendEffect(
                                    new PeekAtTopCardsOfUsedPileAndChooseCardsToTakeIntoHandEffect(action, playerId, valueOfX, 1, 1));
                        } else {
                            action.appendEffect(
                                    new PeekAtTopCardsOfUsedPileAndChooseCardsToTakeIntoHandEffect(action, playerId, valueOfX, 1, 1));
                            action.appendEffect(
                                    new ShuffleUsedPileEffect(action, self, playerId));
                        }
                    }
                });

                return Collections.singletonList(action);
            }

        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Hoth_Sentry, Filters.Sunsdown))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Hoth_Sentry)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Hoth_Sentry, Title.Hoth_Sentry);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Sunsdown)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sunsdown, Title.Sunsdown);
            actions.add(action);
        }

        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.and(Filters.your(playerId), Filters.vehicle_weapon))
                || TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.and(Filters.your(playerId), Filters.artillery_weapon))) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, cardHit)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target card", cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getCardLink(cardHit) + " from being used to satisfy attrition",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotBeUsedToSatisfyAttritionModifier(self, finalTarget),
                                                                GameUtils.getCardLink(cardHit) + " may not be used to satisfy attrition"));
                                            }
                                        }
                                );
                            }
                        });

                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.occupies(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(playerId), Filters.Hoth_location))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}
