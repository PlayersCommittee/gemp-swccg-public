package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Rebel Leadership (V)
 */
public class Card601_158 extends AbstractUsedInterrupt {
    public Card601_158() {
        super(Side.LIGHT, 4, "Rebel Leadership", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("In times of greatest need, the Rebellion relies on the brilliant leadership provided by commanders fighting for freedom.");
        setGameText("Take an admiral or a non-Jedi general into hand from Reserve Deck; reshuffle. OR Once per game, deploy a war room from Reserve Deck; reshuffle. OR If your admiral or non-[Episode I] general is in battle, may add one battle destiny or prevent opponent from drawing more than one battle destiny (their battle destiny draws may not be canceled).");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_3);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.REBEL_LEADERSHIP__UPLOAD_ADMIRAL_OR_GENERAL;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take an admiral or non-Jedi general into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.admiral, Filters.and(Filters.not(Filters.Jedi), Filters.general)), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__REBEL_LEADERSHIP_V__DEPLOY_WAR_ROOM;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy a war room");
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("Deploy a war room from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.war_room, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.admiral, Filters.at(Filters.system)))
                || GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.and(Filters.not(Icon.EPISODE_I), Filters.general), Filters.at(Filters.site)))) {
            final String opponent = game.getOpponent(playerId);
            if (GameConditions.canAddBattleDestinyDraws(game, self)) {

                final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
                action1.setText("Add one battle destiny");
                // Allow response(s)
                action1.allowResponses(
                        new RespondablePlayCardEffect(action1) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action1.appendEffect(
                                        new AddBattleDestinyEffect(action1, 1));
                            }
                        }
                );
                actions.add(action1);
            }

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Limit opponent to one battle destiny");
            // Allow response(s)
            action2.allowResponses("Prevent opponent from drawing more than one battle destiny",
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new DrawsNoMoreThanBattleDestinyEffect(action2, opponent, 1));
                            action2.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action2, new MayNotCancelBattleDestinyModifier(self, opponent), "battle destiny may not be canceled"));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}