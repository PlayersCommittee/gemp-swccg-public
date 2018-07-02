package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Levitation
 */
public class Card4_054 extends AbstractUsedOrLostInterrupt {
    public Card4_054() {
        super(Side.LIGHT, 6, "Levitation");
        setLore("Telekinesis is one of the powers awakened during a Jedi's apprenticeship. Using this ability, a student of the Force can learn to levitate objects.");
        setGameText("USED: Cancel Silence Is Golden or Shut Him Up Or Shut Him Down. LOST: Search your Used Pile and take any one droid, device or Stone Pile you find there into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.or(Filters.Silence_Is_Golden, Filters.Shut_Him_Up_Or_Shut_Him_Down);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Silence_Is_Golden)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Silence_Is_Golden, Title.Silence_Is_Golden);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Shut_Him_Up_Or_Shut_Him_Down)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Shut_Him_Up_Or_Shut_Him_Down, Title.Shut_Him_Up_Or_Shut_Him_Down);
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEVITATION__UPLOAD_DROID_DEVICE_OR_STONE_PILE_FROM_USED_PILE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Used Pile");
            // Allow response(s)
            action.allowResponses("Take a droid, device, or Stone Pile into hand from Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.or(Filters.droid, Filters.device, Filters.Stone_Pile), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}