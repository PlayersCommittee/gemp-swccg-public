package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Macroscan (V)
 */
public class Card210_037 extends AbstractNormalEffect {
    public Card210_037() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Macroscan");
        setLore("Electrobinocular view. Readouts list object's true and relative azimuth, elevation and range. Built-in night vision.");
        setGameText("Deploy on a site. Once per game, you may re-circulate. Once per turn, may cancel and redraw your destiny to power (or a Dark Hours destiny) here, or use X Force to reveal top X cards of your Reserve Deck; may play one in Used Pile (replace other cards in same order).");
        addIcons(Icon.VIRTUAL_SET_10);
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // Deploy on a site
        return Filters.site;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        GameTextActionId gameTextActionId = GameTextActionId.MACROSCAN__ONCE_PER_TURN_ACTION;

        // Once per turn, may cancel and redraw your destiny to power (or a Dark Hours destiny) here

        boolean isRedrawableDestinyToPowerHere = TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && TriggerConditions.isDestinyDrawType(game, effectResult, DestinyType.DESTINY_TO_TOTAL_POWER)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId);

        boolean isRedrawableDarkHoursDestinyHere = TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && TriggerConditions.isDestinyDrawType(game, effectResult, DestinyType.DARK_HOURS_DESTINY)
                && TriggerConditions.isDestinyJustDrawnFor(game, effectResult, playerId, Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId);


        // Check condition(s)
        if (isRedrawableDarkHoursDestinyHere || isRedrawableDestinyToPowerHere) {

            // Cancel and redraw the destiny
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel and re-draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }


        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MACROSCAN__ONCE_PER_GAME_RECIRCULATE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Once per game, re-circulate");
            action.setActionMsg("Once per game, re-circulate.");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RecirculateEffect(action, playerId));

            actions.add(action);
        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.MACROSCAN__ONCE_PER_TURN_ACTION)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            // Use X Force to reveal top X cards of your Reserve Deck;
            // may place one in Used Pile (replace other cards in same order).
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.MACROSCAN__ONCE_PER_TURN_ACTION);
            action.setText("Reveal top X cards of reserve deck");

            int forcePileSize = game.getGameState().getForcePileSize(playerId);

            // Pay cost(s)
            action.appendCost(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose amount of Force to use ", 1, forcePileSize, forcePileSize) {
                                @Override
                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, result));

                                    // Perform result(s)
                                    action.appendEffect(
                                            new RevealTopCardsOfReserveDeckEffect(action, playerId, result) {
                                                @Override
                                                protected void cardsRevealed(final List<PhysicalCard> cards) {
                                                    action.appendEffect(
                                                            new ChooseArbitraryCardsEffect(action, playerId, "Choose card to place in used pile", cards, 0, 1) {
                                                                @Override
                                                                protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {

                                                                    // If user selected a card, place it in used. Otherwise do nothing
                                                                    // The cards are put back automatically.
                                                                    if (!selectedCards.isEmpty()) {
                                                                        PhysicalCard cardToPlaceOnUsedPile = selectedCards.iterator().next();
                                                                        if (cardToPlaceOnUsedPile != null) {
                                                                            action.appendEffect(
                                                                                    new PutCardFromReserveDeckOnTopOfCardPileEffect(action, cardToPlaceOnUsedPile, Zone.USED_PILE, false));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    )
            );

            actions.add(action);
        }

        return actions;
    }
}