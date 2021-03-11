package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Resistance
 * Title: Rey, All Of The Jedi
 */
public class Card214_022 extends AbstractResistance {
    public Card214_022() {
        super(Side.LIGHT, 1, 6, 6, 6, 8, "Rey, All Of The Jedi", Uniqueness.UNIQUE);
        setLore("Female.");
        setGameText("During your control phase, may search your Lost Pile and choose two cards; opponent places one out of play; retrieve the other into hand. Once per game, may deploy a lightsaber on Rey from Reserve Deck; reshuffle. Immune to attrition < 5.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_14);
        addKeywords(Keyword.FEMALE);
        addPersona(Persona.REY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId1 = GameTextActionId.REY_ALL_OF_THE_JEDI__CHOOSE_CARDS_IN_LOST_PILE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId1, Phase.CONTROL)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Choose cards from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(new ChooseCardsFromLostPileEffect(action, playerId, 2, 2) {
                @Override
                protected void cardsSelected(SwccgGame g, final Collection<PhysicalCard> selectedCards) {
                    //probably need to make a new effect and use ArbitraryCardsSelectionDecision in it
                    game.getUserFeedback().sendAwaitingDecision(game.getOpponent(_playerId),
                            new ArbitraryCardsSelectionDecision("Choose a card to place out of play. Your opponent retrieves the other.",
                                    selectedCards, selectedCards, 1, 1) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    List<PhysicalCard> cardsToPlaceOutOfPlay = getSelectedCardsByResponse(result);
                                    if (!cardsToPlaceOutOfPlay.isEmpty()) {
                                        PhysicalCard cardToPlaceOutOfPlay = cardsToPlaceOutOfPlay.iterator().next();
                                        if (cardToPlaceOutOfPlay != null) {
                                            action.appendEffect(
                                                    new PlaceCardOutOfPlayFromOffTableEffect(action, cardToPlaceOutOfPlay));
                                        }
                                        for (PhysicalCard card : selectedCards) {
                                            if (!card.equals(cardToPlaceOutOfPlay)) {
                                                action.appendEffect(
                                                        new RetrieveCardEffect(action, playerId, false, card)
                                                );
                                            }
                                        }
                                    }
                                }
                            });
                }
            });

            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.REY_ALL_OF_THE_JEDI__DOWNLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy lightsaber from Reserve Deck");
            action.setActionMsg("Deploy a lightsaber on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.lightsaber, Filters.sameCardId(self), true, true));
            actions.add(action);
        }
        return actions;
    }
}