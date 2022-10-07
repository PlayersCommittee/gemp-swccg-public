package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelTractorBeamTargetingEffect;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used
 * Title: This Is Our Rebellion
 */
public class Card219_045 extends AbstractUsedInterrupt {
    public Card219_045() {
        super(Side.LIGHT, 5, "This Is Our Rebellion", Uniqueness.UNIQUE);
        setGameText("Peek at up to X cards from the top of your Reserve Deck, where X = number of your Lothal sites on table; " +
                "take one into hand and shuffle your Reserve Deck. " +
                "OR Cancel an attempt to target your starship with a weapon or tractor beam.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        final int yourLothalSiteCount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.your(self), Filters.Lothal_site));

        // Check condition(s)
        if (yourLothalSiteCount > 0
                && GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at up to top " + yourLothalSiteCount + " card" + GameUtils.s(yourLothalSiteCount)+ " of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Peek at up to top " + yourLothalSiteCount + " card" + GameUtils.s(yourLothalSiteCount)+ " of Reserve Deck and take one into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new IntegerAwaitingDecision("Choose number of cards to peek at", 1, yourLothalSiteCount, yourLothalSiteCount) {
                                                @Override
                                                public void decisionMade(final int numToDraw) {
                                                    action.appendEffect(
                                                            new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, numToDraw, 1, 1));
                                                    action.appendEffect(
                                                            new ShuffleReserveDeckEffect(action));
                                                }
                                            }
                                    ));
                        }
                    }
            );


            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.starship), Filters.weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            action.setActionMsg("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTargetedByTractorBeam(game, effect, Filters.and(Filters.your(self), Filters.starship))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel tractor beam targeting");
            action.setActionMsg("Cancel tractor beam targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelTractorBeamTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
