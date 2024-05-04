package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 3
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Twi'lek Advisor (V)
 */
public class Card601_121 extends AbstractUsedOrStartingInterrupt {
    public Card601_121() {
        super(Side.DARK, 5, "Twi'lek Advisor", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("'He's no Jedi.'");
        setGameText("USED: Use 3 Force to deploy a unique (•) alien (for free) from hand or Used Pile; reshuffle. STARTING: Take into hand and/or deploy from Reserve Deck up to three Effects of any kind with 'Jabba' in lore or game text on table from Reserve Deck; reshuffle. Place this Interrupt in Reserve Deck.");
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_3);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TWILEK_ADVISOR_V__DEPLOY_ALIEN;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            if (GameConditions.hasInHand(game, playerId, Filters.and(Filters.unique, Filters.alien, Filters.deployable(self, null, true, 0)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy a unique alien from hand");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 3));
                // Allow response(s)
                action.allowResponses("Deploy a unique alien from hand for free",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardFromHandEffect(action, playerId, Filters.and(Filters.unique, Filters.alien), true));
                            }
                        }
                );
                actions.add(action);
            }

            if (GameConditions.canSearchUsedPile(game, playerId, self, gameTextActionId)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy a unique alien from Used Pile");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 3));
                // Allow response(s)
                action.allowResponses("Deploy a unique alien from Used Pile for free",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardFromUsedPileEffect(action, Filters.and(Filters.unique, Filters.alien), true, true));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Take Effects into hand or deploy from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Take into hand and/or deploy from Reserve Deck up to three Effects of any kind with 'Jabba' in lore or game text",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        Filter jabbaInLoreOrGameText = Filters.or(Filters.loreContains("Jabba"), Filters.loreContains("Jabbas"), Filters.gameTextContains("Jabba"), Filters.gameTextContains("Jabbas"));
                        // Perform result(s)
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Effect_of_any_Kind, jabbaInLoreOrGameText), false) {
                                    @Override
                                    protected void cardTakenIntoHand(final PhysicalCard card) {
                                        if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                            action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                @Override
                                                protected void yes() {
                                                    action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                }
                                            }));
                                        }
                                    }
                                });
                        action.appendEffect(
                                new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 0, 1, Filters.and(Filters.Effect_of_any_Kind, jabbaInLoreOrGameText), false) {
                                    @Override
                                    protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
                                        if (!cards.isEmpty()) {
                                            final PhysicalCard card = cards.iterator().next();
                                            if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                                action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                    }
                                                }));
                                            }
                                        }
                                    }
                                });
                        action.appendEffect(
                                new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 0, 1, Filters.and(Filters.Effect_of_any_Kind, jabbaInLoreOrGameText), false) {
                                    @Override
                                    protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
                                        if (!cards.isEmpty()) {
                                            final PhysicalCard card = cards.iterator().next();
                                            if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                                action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                    }
                                                }));
                                            }
                                        }
                                    }
                                });

                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}