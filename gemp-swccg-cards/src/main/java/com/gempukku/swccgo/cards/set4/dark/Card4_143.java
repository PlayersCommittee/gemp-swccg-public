package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
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

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: HoloNet Transmission
 */
public class Card4_143 extends AbstractUsedOrLostInterrupt {
    public Card4_143() {
        super(Side.DARK, 2, "HoloNet Transmission", Uniqueness.RESTRICTED_2);
        setLore("The Empire uses a real-time hologram communication network. Provides extensive coordination in battles, blockades and searches. Allows rapid deployment and reinforcement.");
        setGameText("USED: Cancel Transmission Terminated. LOST: Take one Imperial or Visage Of The Emperor into hand from Used Pile; reshuffle.");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Transmission_Terminated)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Transmission_Terminated)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Transmission_Terminated, Title.Transmission_Terminated);
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.HOLONET_TRANSMISSION__UPLOAD_IMPERIAL_OR_VISAGE_OF_THE_EMPEROR_FROM_USED_PILE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Used Pile");
            // Allow response(s)
            action.allowResponses("Take an Imperial or Visage Of The Emperor into hand from Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.or(Filters.Imperial, Filters.Visage_Of_The_Emperor), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}