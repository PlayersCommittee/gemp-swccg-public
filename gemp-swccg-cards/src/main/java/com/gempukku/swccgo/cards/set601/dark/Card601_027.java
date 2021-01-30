package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sonic Bombardment (V)
 */
public class Card601_027 extends AbstractUsedInterrupt {
    public Card601_027() {
        super(Side.DARK, 4, Title.Sonic_Bombardment, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Oouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioo-");
        setGameText("Once per game, may take into hand a prison from Reserve Deck; reshuffle. OR Use 1 Force to take into hand a non-droid assassin (or slaver) of ability < 5 from Reserve Deck; reshuffle. OR During battle involving an assassin, each of opponent's battle destiny draws is -1. OR Cancel opponent's attempt to modify the power of a starship piloted by your alien.");
        addIcons(Icon.CLOUD_CITY, Icon.BLOCK_8);
        setAsLegacy(true);
    }

    //TODO Cancel opponent's attempt to modify the power of a starship piloted by your alien.

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SONIC_BOMBARDMENT__UPLOAD_PRISON;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a prison into hand");
            action.appendUsage(new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("Take a prison into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.prison, true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__SONIC_BOMBARDMENT__UPLOAD_ASSASIN_OR_SLAVER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
            && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take an assassin or slaver into hand");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a non-droid assassin (or slaver) of ability < 5 from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.and(Filters.non_droid_character, Filters.or(Filters.assassin, Filters.slaver), Filters.abilityLessThan(5)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.assassin)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Affect opponent's battle destiny draws");
            action.allowResponses("Opponent's battle destiny draws are -1",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action, new EachBattleDestinyModifier(self, -1, game.getOpponent(self.getOwner())), "Battle destiny draws are -1"));
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }
}