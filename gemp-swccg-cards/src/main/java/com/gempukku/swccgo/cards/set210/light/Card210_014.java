package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Character
 * Subtype: Rebel
 * Title: Ezra Bridger
 */
public class Card210_014 extends AbstractRebel {
    public Card210_014() {
        super(Side.LIGHT, 2, 4, 2, 4, 5, Title.Ezra, Uniqueness.UNIQUE);
        setGameText("Power +1 for each Dark Jedi or Jedi on table. At start of opponent's turn, if at opponent's site, opponent chooses: their Force generation is -X this turn, or you may activate up to X Force, where X = number of [Dark Side Force] icons here.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.PADAWAN, Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {

        // Power + 1 for each Dark Jedi and Jedi on table
        Filter darkJediOrJedi = Filters.or(Filters.Dark_Jedi, Filters.Jedi);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnTableEvaluator(self, darkJediOrJedi)));
        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // At start of opponent's turn, if at opponent's site,
        // opponent chooses: their Force generation is -X this
        // turn, or you may activate up to X Force,
        // where X = number of [Dark Side Force] icons here.

        final String playerId = self.getOwner();
        final String opponentId = game.getOpponent(playerId);
        Filter opponentsSite = Filters.and(Filters.site, Filters.opponents(playerId));
        Filter ezraAtOpponentsSite = Filters.and(self, Filters.at(opponentsSite));


        // Check condition(s)
        if (TriggerConditions.isStartOfOpponentsTurn(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, ezraAtOpponentsSite)) {

            // Get all Dark icons here (includes Presence of the force, etc)
            int totalDarkIconsAccumulator = 0;
            for (PhysicalCard card : Filters.filterActive(game, self, Filters.here(self))) {
                totalDarkIconsAccumulator += game.getModifiersQuerying().getIconCount(game.getGameState(), card, Icon.DARK_FORCE);
            }
            final int totalDarkIconsHere = totalDarkIconsAccumulator;

            // ex: "Tom's force generation - 2"
            final String optionReduceGeneration = opponentId + "'s force generation - " + totalDarkIconsHere;

            // ex: "John activates up to 2 force"
            final String optionOpponentActivatesForce = playerId + " activates up to " + totalDarkIconsHere + " force";


            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent must choose force generation option");
            action.setActionMsg("Opponent must choose force generation option.");

            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponentId,
                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{optionReduceGeneration, optionOpponentActivatesForce}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    final GameState gameState = game.getGameState();

                                    if (index == 0) {

                                        // Opponent chooses to generate - X force
                                        gameState.sendMessage(opponentId + " chooses: " + optionReduceGeneration);
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(
                                                        action,
                                                        new TotalForceGenerationModifier(self, -1 * totalDarkIconsHere, opponentId),
                                                        optionReduceGeneration + " from " + Title.Ezra)
                                        );

                                    } else {


                                        // Opponent chooses for LS to activate up-to X force
                                        gameState.sendMessage(opponentId + " chooses: " + optionOpponentActivatesForce);

                                        // Make sure we can activate force. If not, just continue on with no action.
                                        int maxForceToActivate = Math.min(game.getGameState().getReserveDeckSize(playerId), totalDarkIconsHere);
                                        if (!GameConditions.canActivateForce(game, playerId) ||
                                                (game.getGameState().getReserveDeckSize(playerId) == 0)) {

                                            gameState.sendMessage(playerId + " has no force to activate.");
                                            return;
                                        }

                                        // We have force to activate. Let the user pick how much to use
                                        action.appendEffect(
                                                new PlayoutDecisionEffect(action, playerId,
                                                        new IntegerAwaitingDecision("Choose amount of Force to activate", 1, maxForceToActivate, maxForceToActivate) {
                                                            @Override
                                                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new ActivateForceEffect(action, playerId, result));
                                                            }
                                                        }
                                                )
                                        );
                                    }
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }

}
