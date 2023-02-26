package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Objective
 * Title: You Can Either Profit By This... / Or Be Destroyed
 */
public class Card110_004_BACK extends AbstractObjective {
    public Card110_004_BACK() {
        super(Side.LIGHT, 7, Title.Or_Be_Destroyed, ExpansionSet.ENHANCED_JABBAS_PALACE, Rarity.PM);
        setGameText("Immediately retrieve 5 Force (or 10 if Han has power < 4) once per game. While this side up, cancels the game text of Bad Feeling Have I. Your unpiloted starfighters may deploy to exterior Tatooine locations. During your control phase, opponent loses 1 Force for each battleground location occupied by Han, Luke, Leia, Chewie, or Lando. Flip this card if Han is captured or not on table. Place out of play if Tatooine is 'blown away.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Tatooine, true)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OR_BE_DESTROYED__RETRIEVE_FORCE;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            int amountToRetrieve = 5;
            final PhysicalCard han = Filters.findFirstFromAllOnTable(game, Filters.Han);
            if (han != null && game.getModifiersQuerying().getPower(game.getGameState(), han) < 4) {
                amountToRetrieve = 10;
            }

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Force");
            action.setActionMsg("Have " + playerId + " retrieve " + amountToRetrieve + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, amountToRetrieve) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(han);
                        }
                    });
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Han, Filters.not(Filters.captive)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground,
                    Filters.occupiesWith(playerId, self, Filters.or(Filters.Han, Filters.Luke, Filters.Leia, Filters.Chewie, Filters.Lando))));
            if (numForce > 0) {

                if (numForce > 3
                        && game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.OR_BE_DESTROYED__FORCE_LOSS_MAY_NOT_EXCEED_THREE_OR_BE_REDUCED)) {
                    // Force loss from Or Be Destroyed may not exceed 3
                    numForce = 3;
                }

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));

                // Perform result(s)
                if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.OR_BE_DESTROYED__FORCE_LOSS_MAY_NOT_EXCEED_THREE_OR_BE_REDUCED)) {
                    // Force loss from Or Be Destroyed may not be reduced
                    action.appendEffect(new LoseForceFromReserveDeckEffect(action, opponent, numForce, true));
                } else if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__OR_BE_DESTROYED__FORCE_LOSS)) {
                    // Force loss from Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2
                    action.appendEffect(new LoseForceFromReserveDeckEffect(action, opponent, numForce, 2));
                } else {
                    action.appendEffect(
                            new LoseForceEffect(action, opponent, numForce));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Bad_Feeling_Have_I));
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, Filters.and(Filters.your(self), Filters.unpiloted, Filters.starfighter),
                Filters.exterior_Tatooine_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground,
                    Filters.occupiesWith(playerId, self, Filters.or(Filters.Han, Filters.Luke, Filters.Leia, Filters.Chewie, Filters.Lando))));
            if (numForce > 0) {

                if (numForce > 3
                        && game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.OR_BE_DESTROYED__FORCE_LOSS_MAY_NOT_EXCEED_THREE_OR_BE_REDUCED)) {
                    // Force loss from Or Be Destroyed may not exceed 3
                    numForce = 3;
                }

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.OR_BE_DESTROYED__FORCE_LOSS_MAY_NOT_EXCEED_THREE_OR_BE_REDUCED)) {
                    // Force loss from Or Be Destroyed may not be reduced
                    action.appendEffect(new LoseForceFromReserveDeckEffect(action, opponent, numForce, true));
                } else if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__OR_BE_DESTROYED__FORCE_LOSS)) {
                    // Force loss from ...Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2
                    action.appendEffect(new LoseForceFromReserveDeckEffect(action, opponent, numForce, 2));
                } else {
                    action.appendEffect(
                            new LoseForceEffect(action, opponent, numForce));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}