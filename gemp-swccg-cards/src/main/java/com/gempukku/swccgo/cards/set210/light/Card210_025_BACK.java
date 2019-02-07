package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveDeployCostModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawCompleteResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Objective
 * Title: The Hyperdrive Generator's Gone (V) / We'll Need A New One (V)
 */
public class Card210_025_BACK extends AbstractObjective {
    public Card210_025_BACK() {
        super(Side.LIGHT, 7, Title.Well_Need_A_New_One);
        setVirtualSuffix(true);
        setGameText("While this side up, your unique (•) Republic characters are power +1 and forfeit +2. Aliens may not have their deploy cost modified to Tatooine locations. Whenever you complete a non-substituted battle destiny draw, may retrieve 1 Force (Force retrieved in this way may be taken into hand). Once during opponent's turn, if Queen's Royal Starship at a system, may activate up to 2 Force. During your control phase, opponent loses 1 Force for each battleground occupied by Amidala or Jar Jar.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourUniqueRepublicCharacter = Filters.and(Filters.your(self), Filters.unique, Filters.Republic_character);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourUniqueRepublicCharacter, 1));
        modifiers.add(new ForfeitModifier(self, yourUniqueRepublicCharacter, 2));

        Condition atTatooineLocations = new AtCondition(self, Filters.Tatooine_location);
        modifiers.add(new MayNotHaveDeployCostModifiedModifier(self, Filters.alien, atTatooineLocations, null));

        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyDrawComplete(game, effectResult, playerId)) {
            DestinyDrawCompleteResult result = (DestinyDrawCompleteResult) effectResult;
            if (result.getCard() != null) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1) {
                            @Override
                            public boolean mayBeTakenIntoHand() {
                                return true;
                            }

                            @Override
                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                return Filters.filterActive(game, null, Filters.and(Filters.your(playerId), Filters.participatingInBattle));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Queens_Royal_Starship, Filters.at(Filters.system)))
                && GameConditions.canActivateForce(game, playerId)) {
            int maxForceToActivate = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxForceToActivate > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Activate up to 2 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Activate " + result + " Force");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result));
                                    }
                                }
                        )
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            final TopLevelGameTextAction action = (TopLevelGameTextAction) opponentLosesOneForceForEachBattlegroundOccupiedByAmidalaOrJarJar(game, self, playerId, gameTextActionId, gameTextSourceCardId, true);
            if (action != null) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            final RequiredGameTextTriggerAction action = (RequiredGameTextTriggerAction) opponentLosesOneForceForEachBattlegroundOccupiedByAmidalaOrJarJar(game, self, playerId, gameTextActionId, gameTextSourceCardId, false);
            if (action != null) {
                actions.add(action);
            }
        }
        return actions;
    }

    private AbstractGameTextAction opponentLosesOneForceForEachBattlegroundOccupiedByAmidalaOrJarJar(SwccgGame game, PhysicalCard self, String playerId, GameTextActionId gameTextActionId, int gameTextSourceCardId, boolean isTopLevelAction) {
        int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.or(Filters.Jar_Jar, Filters.Amidala))));
        if (numForce > 0) {
            AbstractGameTextAction action;
            if (isTopLevelAction) {
                action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            } else {
                action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            }
            action.setText("Make opponent lose " + numForce + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), numForce));
            return action;
        }
        return null;
    }
}
