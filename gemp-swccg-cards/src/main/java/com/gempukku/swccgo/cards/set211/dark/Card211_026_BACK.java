package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

public class Card211_026_BACK extends AbstractObjective {
    public Card211_026_BACK() {
        super(Side.DARK, 7, "A Valuable Hostage");
        setGameText("While this side up, immunity to attrition of opponent's Jedi, starships, and vehicles is limited to < 5. During your control phase, if your [Separatist] character with Insidious Prisoner, opponent loses 1 Force. Once during your turn, may search your Force Pile and reveal any one card; reshuffle. Opponent may lose 2 Force to place that card on bottom of your Used Pile; otherwise, take it into hand.\n" +
                "Flip this card if Insidious Prisoner is not at an Invisible Hand site.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    // TODO TEST Immunity limited to < 5
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter opponentsJediStarshipsVehicles = Filters.and(Filters.opponents(self.getOwner()), Filters.or(Filters.Jedi, Filters.starship, Filters.vehicle));
        Filter cardsWithMoreThan5ITA = Filters.and(opponentsJediStarshipsVehicles, Filters.immunityToAttritionMoreThan(5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, cardsWithMoreThan5ITA, 5));

        return modifiers;
    }

    private Filter yourSeparatistCharacterWithInsidiousPrisoner(String playerId) {
        Filter yourSeparatistCharacter = Filters.and(Filters.your(playerId), Filters.character, Icon.SEPARATIST);
        return Filters.and(yourSeparatistCharacter, Filters.at(Filters.hasAttached(Filters.Insidious_Prisoner)));
    }

    // TODO TEST Opponent loses 1 force -- TLA
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.A_STUNNING_MOVE__CONTROL_PHASE_DAMAGE;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int NUM_FORCE_LOSS = 1;
            if (GameConditions.canSpot(game, self, yourSeparatistCharacterWithInsidiousPrisoner(playerId))) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + NUM_FORCE_LOSS + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), NUM_FORCE_LOSS));
                actions.add(action);
            }
        }

        // TODO TEST Force pile reveal
        gameTextActionId = GameTextActionId.A_STUNNING_MOVE__REVEAL_CARD_FROM_FORCE_PILE;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card face down on side of table");
            action.setActionMsg("Place a card face down on side of table");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new ChooseCardFromForcePileEffect(action, playerId) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final GameState gameState = game.getGameState();
                            final String opponent = game.getOpponent(playerId);
                            action.addAnimationGroup(selectedCard);
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, opponent,
                                            new MultipleChoiceAwaitingDecision("Choose result", new String[]{"Lose 2 force: Place card on bottom of used pile", "Opponent takes card into hand"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (index == 0) {
                                                        gameState.sendMessage(opponent + " chooses to lose 2 Force and place card on bottom of Used Pile");
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 2));
                                                        action.appendEffect(
                                                                new PutCardFromCardPileOnBottomOfCardPileEffect(action, playerId, selectedCard, false));
                                                    } else {
                                                        gameState.sendMessage(opponent + " chooses to place card in opponent's hand");
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromForcePileEffect(action, playerId, selectedCard, false));
                                                    }
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        GameTextActionId gameTextActionId = GameTextActionId.A_STUNNING_MOVE__CONTROL_PHASE_DAMAGE;

        // TODO TEST Opponent loses 1 force -- Req'd trigger
        if (GameConditions.isOnceDuringYourPhase(game, self, self.getOwner(), gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int NUM_FORCE_LOSS = 1;
            if (GameConditions.canSpot(game, self, yourSeparatistCharacterWithInsidiousPrisoner(self.getOwner()))) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + NUM_FORCE_LOSS + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(self.getOwner()), NUM_FORCE_LOSS));
                actions.add(action);
            }
        }

        PhysicalCard insidiousPrisoner = Filters.findFirstActive(game, self, Filters.Insidious_Prisoner);
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (!GameConditions.isAttachedTo(game, insidiousPrisoner, Filters.Invisible_Hand_site))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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
