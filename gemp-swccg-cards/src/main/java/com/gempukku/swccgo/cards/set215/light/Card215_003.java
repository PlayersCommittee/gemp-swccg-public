package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio) (V)
 */
public class Card215_003 extends AbstractDroid {
    public Card215_003() {
        super(Side.LIGHT, 3, 2, 1, 4, "C-3PO (See-Threepio)", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Cybot Galactica 3PO human-cyborg relations droid. Fluent in over six million forms of communication. 112 years old. Has never been memory-wiped... as far as he knows.");
        setGameText("Once per turn, may place a card from hand on Used Pile to activate 1 Force (or to [upload] [A New Hope] R2-D2). During your control phase, if with a Rebel at an interior mobile battleground, opponent loses 1 Force.");
        addPersona(Persona.C3PO);
        addModelType(ModelType.PROTOCOL);
        addIcon(Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.C_3PO_V__PLACE_CARD_ON_USED_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {

            if (GameConditions.canActivateForce(game, playerId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Activate 1 force");
                action.setActionMsg("Put card on Used Pile to activate 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PutCardFromHandOnUsedPileEffect(action, playerId));
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, 1));
                actions.add(action);
            }

            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Upload [A New Hope] R2-D2");
                action.setActionMsg("Put card on Used Pile to upload [A New Hope] R2-D2");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PutCardFromHandOnUsedPileEffect(action, playerId));
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.R2D2, Filters.icon(Icon.A_NEW_HOPE)), true));
                actions.add(action);
            }


        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isWith(game, self, Filters.Rebel)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.interior_mobile_site, Filters.battleground))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 force");
            action.setActionMsg("Make opponent lose 1 force");
            // Perform result(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 1));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isWith(game, self, Filters.Rebel)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.interior_mobile_site, Filters.battleground))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }

        return null;
    }
}
