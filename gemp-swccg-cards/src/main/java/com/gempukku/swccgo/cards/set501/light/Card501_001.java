package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Resistance
 * Title: Rey, All Of The Jedi
 */
public class Card501_001 extends AbstractResistance {
    public Card501_001() {
        super(Side.LIGHT, 1, 6, 6, 6, 8, "Rey, All Of The Jedi", Uniqueness.UNIQUE);
        setLore("Female.");
        setGameText("During your control phase, may search your Lost Pile and choose two cards; opponent places one out of play; retrieve the other into hand. Once per game, may deploy a lightsaber on Rey from Reserve Deck; reshuffle. Immune to attrition < 5.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_14);
        addKeywords(Keyword.FEMALE);
        addPersona(Persona.REY);
        setTestingText("•Rey, All Of The Jedi");
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


        System.out.println("huh");
        GameTextActionId gameTextActionId1 = GameTextActionId.REY_ALL_OF_THE_JEDI__CHOOSE_CARDS_IN_LOST_PILE;
        System.out.println(game.getGameState().getCurrentPhase().getHumanReadable());
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId1, Phase.CONTROL)) {
            System.out.println("what");
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
                        action.appendEffect(new ChooseCardEffect(action, game.getOpponent(playerId), "Choose a card to place out of play. The other will go to your opponent's hand.", selectedCards) {
                            protected void cardSelected(PhysicalCard cardSelected) {
                                action.appendEffect(new PlaceCardOutOfPlayFromLostPileEffect(action, game.getOpponent(playerId), playerId, cardSelected, false));
                                for(PhysicalCard card: selectedCards) {
                                    if(!card.equals(cardSelected)) {
                                        action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId, card));
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