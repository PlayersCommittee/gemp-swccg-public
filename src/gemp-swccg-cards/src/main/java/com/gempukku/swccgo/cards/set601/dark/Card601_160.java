package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Stunning Leader (V)
 */
public class Card601_160 extends AbstractUsedInterrupt {
    public Card601_160() {
        super(Side.DARK, 4, Title.Stunning_Leader, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Stormtroopers are trained to stun, not kill, priority targets in order to split enemy forces and hold the leaders for interrogation. Stun effects wear off, requiring efficient action.");
        setGameText("Use 1 Force to deploy (or retrieve) a Mistryl from Reserve Deck; reshuffle. OR If your Mistryl present during battle, use 1 Force to reveal the top three cards of opponent's Reserve Deck. Add the destiny number of the highest-destiny male revealed (if any) to your total power. Replace revealed cards.");
        addIcons(Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__STUNNING_LEADER_V__DEPLOY_OR_RETRIEVE_MISTRYL;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy a Mistryl");
            action.setActionMsg("Deploy a Mistryl from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Deploy a Mistryl" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Mistryl, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasLostPile(game, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Retrieve a Mistryl");
            action.setActionMsg("Retrieve a Mistryl");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Deploy a Mistryl" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Mistryl));
                        }
                    }
            );
            actions.add(action);
        }

        final String opponent = game.getOpponent(playerId);
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.presentInBattle, Filters.your(self), Filters.Mistryl))
                && GameConditions.hasReserveDeck(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Reveal cards from opponent's Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Reveal cards from opponent's Reserve Deck" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealTopCardsOfReserveDeckEffect(action, playerId, opponent, 3) {
                                        @Override
                                        protected void cardsRevealed(List<PhysicalCard> cards) {
                                            if(cards.size()==3) {
                                                float result = 0;
                                                for(PhysicalCard card:cards) {
                                                    if (Filters.male.accepts(game, card)) {
                                                        result = Math.max(result, game.getModifiersQuerying().getDestiny(game.getGameState(), card));
                                                    }
                                                }
                                                if (result>0) {
                                                    action.appendEffect(new ModifyTotalPowerUntilEndOfBattleEffect(action, result, playerId, "Adds " + GuiUtils.formatAsString(result) + " to total power"));
                                                }
                                            }
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