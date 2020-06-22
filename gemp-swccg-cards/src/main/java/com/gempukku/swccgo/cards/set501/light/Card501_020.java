package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo
 */
public class Card501_020 extends AbstractRebel {
    public Card501_020() {
        super(Side.LIGHT, 1, 2, 3, 3, 5, "Han Solo", Uniqueness.UNIQUE);
        setLore("Smuggler, gambler, and thief. Correlian.");
        setGameText("Adds 2 to power and maneuver of anything he pilots. While piloting Falcon, may add a destiny to power or attrition (both if Chewie here). During battle here, may peek at top 2 cards of any Reserve deck and “smuggle” one of them (move to top of owner's Used Pile).");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.THIEF);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
        setTestingText("Han Solo");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.OTHER_CARD_ACTION_1;
        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isPiloting(game, self, Filters.Falcon)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId1)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId1);
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            if (GameConditions.isHere(game, self, Filters.Chewie)) {
                action.setText("Add destiny to both Power and Attrition");
                action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
                action.appendEffect(new AddDestinyToAttritionEffect(action, 1));
            } else {
                action.setText("Add destiny to either Power and Attrition");
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Add destiny to power or attrition?", new String[]{"Power", "Attrition"}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (index == 0) {
                                            action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
                                        } else {
                                            action.appendEffect(new AddDestinyToAttritionEffect(action, 1));
                                        }
                                    }
                                })
                );
            }
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId2)) {
            final int numCardsToPeekAt = 2;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("'Smuggle' a card to used pile");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.setActionMsg("Peek at top " + numCardsToPeekAt + " cards of " + cardPileOwner + "'s Reserve Deck");
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckEffect(action, playerId, cardPileOwner, numCardsToPeekAt) {
                                        @Override
                                        protected void cardsPeekedAt(final List<PhysicalCard> peekedAtCards) {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId, new ArbitraryCardsSelectionDecision("Choose card in place in Used Pile", peekedAtCards, 1, 1) {
                                                        @Override
                                                        public void decisionMade(String result) throws DecisionResultInvalidException {
                                                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                                            if (!selectedCards.isEmpty()) {
                                                                action.appendEffect(
                                                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, selectedCards.get(0), Zone.USED_PILE, true)
                                                                );
                                                            }
                                                        }
                                                    }
                                                    )
                                            );
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
