package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Babu Frik
 */
public class Card219_029 extends AbstractAlien {
    public Card219_029() {
        super(Side.LIGHT, 4, 1, 0, 2, 3, "Babu Frik", Uniqueness.UNIQUE);
        setLore("Droidsmith. Spice Runner. Anzellan.");
        setGameText("When deployed, may search your Lost Pile and move one card there to the top of that pile (if that card is a droid, may retrieve it into hand). " +
                    "Once per game, may use 2 Force to target a droid here; for remainder of turn, target's game text is canceled.");
        addKeywords(Keyword.DROIDSMITH, Keyword.SPICE_RUNNER);
        setSpecies(Species.ANZELLAN);
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BABU_FRIK__SEARCH_LOST_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search your Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new ChooseCardFromPileEffect(action, playerId, Zone.LOST_PILE, playerId) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on top of " + Zone.LOST_PILE.getHumanReadable();
                        }
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            if (selectedCard != null) {
                                String cardInfo = GameUtils.getCardLink(selectedCard);
                                action.setActionMsg("Move " + cardInfo + " to the top of " + Zone.LOST_PILE.getHumanReadable());
                                action.appendEffect(
                                        new PutCardFromLostPileOnTopOfCardPileEffect(action, selectedCard, Zone.LOST_PILE, false));

                                if (Filters.droid.accepts(game, selectedCard)) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new YesNoDecision("Do you want retrieve " + GameUtils.getCardLink(selectedCard) + " into hand?") {
                                                        @Override
                                                        protected void yes() {
                                                            action.setActionMsg("Retrieve " + GameUtils.getCardLink(selectedCard) + " into hand");
                                                            action.appendEffect(
                                                                    new RetrieveCardIntoHandEffect(action, playerId, false));
                                                        }
                                                        @Override
                                                        protected void no() {
                                                            game.getGameState().sendMessage(playerId + " chooses to not to retrieve " + GameUtils.getCardLink(selectedCard) + " into hand");
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BABU_FRIK__CANCEL_DROID_GAME_TEXT;
        final Filter targetFilter = Filters.and(Filters.droid, Filters.here(self));

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel a droid's game text");
            action.setActionMsg("Cancel the game text of a droid here for remainder of turn");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target droid", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text for remainder of turn",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}