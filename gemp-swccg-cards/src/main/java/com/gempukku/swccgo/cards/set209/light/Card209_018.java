package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: Stardust
 */
public class Card209_018 extends AbstractNormalEffect {
    public Card209_018() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Stardust, Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Data Vault. At any time, may relocate Stardust to your spy present. During your control phase, if on your spy at a battleground you occupy, opponent loses 2 Force. If about to leave table, relocate to Data Vault (if possible). [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.R2D2;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            PhysicalCard duneSea = Filters.findFirstFromTopLocationsOnTable(game, Filters.Dune_Sea);
            if (duneSea != null) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to Dune Sea");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(duneSea));
                action.addAnimationGroup(duneSea);
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(self);
                                for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                    result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                                }
                            }
                        });
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, duneSea));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter yourCharacterFilter = Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.hasAttached(self)), Filters.here(self));

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.Dune_Sea)
                && GameConditions.canTarget(game, self, yourCharacterFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate to your character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", yourCharacterFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AttachCardFromTableEffect(action, self, targetedCard));
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

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.STOLEN_DATA_TAPES__UPLOAD_CARD;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && (GameConditions.isAtSystem(game, self, Title.Alderaan)
                || GameConditions.isAtLocation(game, self, Filters.and(Filters.system, Filters.blown_away)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Relocate to table");
            // Perform result(s)
            action.appendEffect(
                    new RelocateCardToSideOfTableEffect(action, self, playerId));
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Do you want to take a card into hand from Reserve Deck?") {
                                    @Override
                                    protected void yes() {
                                        game.getGameState().sendMessage(playerId + " chooses to take a card into hand from Reserve Deck");
                                        action.appendEffect(
                                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
                                    }
                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(playerId + " chooses to not take a card into hand from Reserve Deck");
                                    }
                                }
                        )
                );
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}