package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutRandomCardsFromHandOnLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Step Backward
 */
public class Card11_030 extends AbstractLostInterrupt {
    public Card11_030() {
        super(Side.LIGHT, 5, "A Step Backward", Uniqueness.UNIQUE);
        setLore("The clashing of Podracers slowed the pace of the race as Anakin and Sebulba approached the finish line.");
        setGameText("Place the top race destiny of all Podracers in owner's Used Pile. OR Place 3 cards from hand (random choice) into your Lost Pile to take any one character from your Lost Pile into hand.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();

        final Filter topRaceDestiniesFilter = Filters.and(Filters.topRaceDestiny, Filters.stackedOn(self, Filters.and(Filters.Podracer, Filters.canBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, topRaceDestiniesFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place top race destinies in Used Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            Collection<PhysicalCard> topRaceDestinies = Filters.filterStacked(game, topRaceDestiniesFilter);
                            if (!topRaceDestinies.isEmpty()) {
                                action.appendEffect(
                                        new PutStackedCardsInUsedPileEffect(action, playerId, topRaceDestinies, false));
                            }
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.A_STEP_BACKWARD__UPLOAD_CHARACTER_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, 3, Filters.not(self))
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take character into hand from Lost Pile");
            // Pay cost(s)
            action.appendCost(
                    new PutRandomCardsFromHandOnLostPileEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses("Take a character into hand from Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.character, false));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}