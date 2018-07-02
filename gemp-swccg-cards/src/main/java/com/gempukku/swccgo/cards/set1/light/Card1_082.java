package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Gift Of The Mentor
 */
public class Card1_082 extends AbstractLostInterrupt {
    public Card1_082() {
        super(Side.LIGHT, 5, Title.Gift_Of_The_Mentor, Uniqueness.UNIQUE);
        setLore("Luke relied on Obi-Wan's knowledge and advice to learn the ways of the Force. Obi-Wan continued to counsel Luke long after the old Jedi's apparent 'death.'");
        setGameText("If Luke and Obi-Wan or Yoda are in a battle together, you may add two battle destiny. OR Use 1 Force to search through your Reserve Deck and take any one lightsaber into your hand. Shuffle deck, cut and replace.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.ObiWan, Filters.Yoda))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            final Filter lukeFilter = Filters.and(Filters.Luke, Filters.participatingInBattle);
            final Filter obiWanOrYodaFilter = Filters.and(Filters.or(Filters.ObiWan, Filters.Yoda), Filters.participatingInBattle);
            if (GameConditions.canTarget(game, self, lukeFilter)
                    && GameConditions.canTarget(game, self, obiWanOrYodaFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard lukeTargeted) {
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan or Yoda", obiWanOrYodaFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard obiWanOrYodaTargeted) {
                                                action.addAnimationGroup(lukeTargeted, obiWanOrYodaTargeted);
                                                // Allow response(s)
                                                action.allowResponses("Add two battle destiny by targeting " + GameUtils.getAppendedNames(Arrays.asList(lukeTargeted, obiWanOrYodaTargeted)),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AddBattleDestinyEffect(action, 2));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.GIFT_OF_THE_MENTOR__UPLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take lightsaber into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a lightsaber into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.lightsaber, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}