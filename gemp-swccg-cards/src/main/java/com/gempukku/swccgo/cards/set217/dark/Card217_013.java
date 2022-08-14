package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Lando Calrissian, Vader's Broker
 */
public class Card217_013 extends AbstractAlienImperial {
    public Card217_013() {
        super(Side.DARK, 1, 2, 3, 3, 3, "Lando Calrissian, Vader's Broker", Uniqueness.UNIQUE);
        setLore("Scoundrel and gambler. Petty administrator of a small Tibanna gas mining operation. Easily coerced. Has problems of his own. Had dealings with the Tonnika sisters - twice.");
        setGameText("Once during opponent's turn, if with an Imperial on Cloud City, may activate 1 Force. Once during your turn, if with an alien on Cloud City, may draw top card of Reserve Deck. Once per game, may place a non-[Immune to Alter] Effect in owner's Used Pile.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CLOUD_CITY, Icon.VIRTUAL_SET_17);
        addPersona(Persona.LANDO);
        addKeywords(Keyword.GAMBLER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Once during opponent's turn, if with an Imperial on Cloud City, may activate 1 Force.
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isWith(game, self, Filters.and(Filters.Imperial, Filters.on_Cloud_City))
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");

            action.appendUsage(
                    new OncePerTurnEffect(action));

            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }


        // Once during your turn, if with an alien on Cloud City, may draw top card of Reserve Deck.
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isWith(game, self, Filters.and(Filters.alien, Filters.on_Cloud_City))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");

            action.appendUsage(
                    new OncePerTurnEffect(action));

            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 1));
            actions.add(action);
        }

        // Once per game, may place a non-[Immune to Alter] Effect in owner's Used Pile.
        gameTextActionId = GameTextActionId.LANDO_CALRISSIAN_VADERS_BROKER__PLACE_EFFECT_IN_USED_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.and(Filters.not(Filters.immune_to_Alter), Filters.Effect))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Effect in Used Pile");
            action.setActionMsg("Place a non-[Immune to Alter] Effect in owner's Used Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a non-[Immune to Alter] Effect to place in owner's Used Pile", Filters.and(Filters.not(Filters.immune_to_Alter), Filters.Effect)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                        }
                    });
                }
            });
            actions.add(action);
        }

        return actions;
    }
}
