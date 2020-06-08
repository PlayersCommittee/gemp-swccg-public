package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sonic Bombardment
 */
public class Card5_155 extends AbstractUsedOrLostInterrupt {
    public Card5_155() {
        super(Side.DARK, 4, Title.Sonic_Bombardment, Uniqueness.UNIQUE);
        setLore("Oouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioo-");
        setGameText("USED: Search your Reserve Deck, take any one prison into hand and reshuffle. LOST: If you have an opponent's alien captive at a prison, opponent loses 3 Force.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SONIC_BOMBARDMENT__UPLOAD_PRISON;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take a prison into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
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

        final String opponent = game.getOpponent(playerId);
        TargetingReason targetingReason = TargetingReason.TO_BE_TORTURED;
        Filter captiveFilter = Filters.and(Filters.opponents(self), Filters.alien, Filters.captive, Filters.at(Filters.prison));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, targetingReason, captiveFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make opponent lose 3 Force");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, targetingReason, captiveFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard captive) {
                            action.addAnimationGroup(captive);
                            // Allow response(s)
                            action.allowResponses("Make opponent lose 3 Force by torturing " + GameUtils.getCardLink(captive),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}