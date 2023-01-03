package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: The Mythrol
 */
public class Card200_016_BACK extends AbstractAlien {
    public Card200_016_BACK() {
        super(Side.LIGHT, 7, 0, 2, 1, 2, Title.The_Mythrol, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setLore("Mythrol accountant.");
        setGameText("If either player just deployed a card with ability here, you may use 1 Force to place The Mythrol out of play; if card was Din Djarin or a bounty hunter, you may activate 2 Force. If about to leave table, place out of play.");
        addKeyword(Keyword.ACCOUNTANT);
        setSpecies(Species.MYTHROL);
        addIcons(Icon.VIRTUAL_SET_0);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
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
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployedToLocation(game, effectResult, Filters.hasAbility, Filters.here(self))
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.title("The Mythrol"))
                && GameConditions.canUseForce(game, playerId, 1)) {

            final boolean dinDjarinOrBountyHunter = TriggerConditions.justDeployedToLocation(game, effectResult, Filters.or(Filters.Din, Filters.bounty_hunter), Filters.here(self));

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place The Mythrol out of play");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target The Mythrol to place out of play", Filters.title("The Mythrol")) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.appendCost(
                            new UseForceEffect(action, playerId, 1));
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard theMythrol = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromTableEffect(action, theMythrol));
                            if (dinDjarinOrBountyHunter) {
                                if (GameConditions.canActivateForce(game, playerId)) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Activate 2 Force?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendEffect(
                                                            new ActivateForceEffect(action, playerId, 2));
                                                }

                                                @Override
                                                protected void no() {
                                                    action.appendEffect(
                                                            new SendMessageEffect(action, playerId + " chooses to not activate Force"));
                                                }
                                            }));
                                } else {
                                    action.appendEffect(
                                            new SendMessageEffect(action, playerId + " may not activate Force"));
                                }
                            }
                        }
                    });

                }

                @Override
                protected boolean getUseShortcut() {
                    return true;
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
