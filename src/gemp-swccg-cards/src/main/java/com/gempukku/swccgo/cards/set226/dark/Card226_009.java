package com.gempukku.swccgo.cards.set226.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Ommni Box & It's Worse (V)
 */
public class Card226_009 extends AbstractUsedOrLostInterrupt {
    public Card226_009() {
        super(Side.DARK, 5, Title.Ommni_Box_Its_Worse, Uniqueness.UNRESTRICTED, ExpansionSet.SET_26, Rarity.V);
        addComboCardTitles(Title.Ommni_Box, Title.Its_Worse);
        setGameText("USED: For remainder of turn, you lose no Force to the following cards on table (if any): A Good Blaster At Your Side, No Disintegrations!, Stardust, and They Will Be Lost And Confused. OR Cancel Blast The Door, Kid! or It Could Be Worse. (Immune to It's A Hit!) OR Shuffle any player's Reserve Deck or Lost Pile. LOST: If opponent just lost a battle, they lose 2 Force.");
        addIcons(Icon.REFLECTIONS_II, Icon.VIRTUAL_SET_26);
        setVirtualSuffix(true);
    }
    
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter pingCards = Filters.or(Filters.A_Good_Blaster_At_Your_Side, Filters.No_Disintegrations,
                Filters.Stardust, Filters.They_Will_Be_Lost_And_Confused);
        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Prevent Force loss until end of turn");
        action.setImmuneTo(Title.Its_A_Hit);
        // Allow response(s)
        action.allowResponses("Prevent Force loss from A Good Blaster At Your Side, No Disintegrations!, Stardust, and They Will Be Lost And Confused for remainder of turn",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new NoForceLossFromCardModifier(self, pingCards, playerId),
                                        "Prevents Force loss from A Good Blaster At Your Side, No Disintegrations!, Stardust, and They Will Be Lost And Confused"));
                    }
                }
        );
        actions.add(action);

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent)
                || GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)) {

            final PlayInterruptAction action1 = new PlayInterruptAction(game, self, CardSubtype.USED);
            action1.setText("Shuffle card pile");
            // Choose target(s)
            action1.appendTargeting(
                    new ChooseExistingCardPileEffect(action1, playerId, Filters.or(Zone.RESERVE_DECK, Zone.LOST_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            // Allow response(s)
                            action1.allowResponses("Shuffle " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondablePlayCardEffect(action1) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action1.appendEffect(
                                                    new ShufflePileEffect(action1, cardPileOwner, cardPile));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action1);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter cancelInterrupts = Filters.or(Filters.Blast_The_Door_Kid, Filters.It_Could_Be_Worse);
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, cancelInterrupts)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Its_A_Hit);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, opponent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make opponent lose 2 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
