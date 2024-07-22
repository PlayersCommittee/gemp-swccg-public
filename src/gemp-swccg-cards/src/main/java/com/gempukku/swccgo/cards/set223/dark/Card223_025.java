package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardFromVoidOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: The Empire's Back (V)
 */
public class Card223_025 extends AbstractUsedOrLostInterrupt {
    public Card223_025() {
        super(Side.DARK, 3, "The Empire's Back", Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setLore("No star system will dare oppose the Emperor now.");
        setGameText("USED: Deploy Empire's New Order or Overseeing It Personally from Reserve Deck; reshuffle. LOST: Once per game, choose: if two Imperial leaders (or Xizor) in battle, recirculate. OR Place opponent's just-played Interrupt out of play.");
        addIcons(Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPIRES_BACK_V__DOWNLOAD_EFFECTS;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy a card from Reserve Deck");
            action.setActionMsg("Deploy Empire's New Order or Overseeing It Personally from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Empires_New_Order, Filters.Overseeing_It_Personally), true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.THE_EMPIRES_BACK_V__OUT_OF_PLAY_OR_RECIRCULATE;

        // Check Condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId2) 
                && (GameConditions.isDuringBattle(game)
                && (GameConditions.canTarget(game, self, Filters.and(Filters.Xizor, Filters.participatingInBattle)) 
                    || GameConditions.canTarget(game, self, 2, Filters.and(Filters.Imperial_leader, Filters.participatingInBattle))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2, CardSubtype.LOST);
            action.setText("Re-circulate");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RecirculateEffect(action, playerId));                                            
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPIRES_BACK_V__OUT_OF_PLAY_OR_RECIRCULATE;
        final PhysicalCard cardBeingPlayed = ((RespondablePlayCardEffect) effect).getCard();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.interruptCanBePlacedOutOfPlay(game, cardBeingPlayed)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Place Interrupt out of play");
            action.allowResponses("Place a just-played interrupt out of play",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                            new PlaceCardFromVoidOutOfPlayEffect(action, cardBeingPlayed));
                    }
                }
            );
        }
        return actions;
    }
}
