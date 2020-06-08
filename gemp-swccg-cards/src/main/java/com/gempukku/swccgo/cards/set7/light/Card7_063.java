package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Subtype: Immediate
 * Title: Heroic Sacrifice
 */
public class Card7_063 extends AbstractImmediateEffect {
    public Card7_063() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Heroic_Sacrifice);
        setLore("The Alliance lost many fine soldiers at conflicts such as the Battle of Hoth. The Rebel High Command is aware of the cost of freedom from Imperial tyranny.");
        setGameText("If you just forfeited a Rebel of ability > 2 from a battle at a battleground, deploy on that location; place that Rebel out of play and retrieve 2 Force for each Heroic Sacrifice on table. Immediate Effect canceled if opponent controls this location. (Immune to Control.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2)), Filters.battleground)) {
            PhysicalCard rebel = ((LostFromTableResult) effectResult).getCard();
            PhysicalCard location = ((LostFromTableResult) effectResult).getFromLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(location), null);
            if (action != null) {
                // Remember the Rebel forfeited
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(rebel)));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Rebel;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard location = game.getModifiersQuerying().getLocationHere(game.getGameState(), self);
            PhysicalCard rebel = self.getWhileInPlayData().getPhysicalCard();
            int forceToRetrieve = 2 * Filters.countActive(game, self, Filters.and(Filters.Heroic_Sacrifice, Filters.mayContributeToForceRetrieval));

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Place Rebel out of play and retrieve Force");
            action.setActionMsg("Place " + GameUtils.getCardLink(rebel) + " out of play and retrieve " + forceToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, rebel, false));
            if (!Filters.playersCardsAtLocationMayContributeToForceRetrieval(playerId).accepts(game, location)) {
                action.appendEffect(
                        new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
            }
            else {
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, forceToRetrieve));
            }
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameLocation(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}