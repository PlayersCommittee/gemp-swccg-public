package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Put All Sections On Alert
 */
public class Card7_259 extends AbstractUsedOrLostInterrupt {
    public Card7_259() {
        super(Side.DARK, 6, "Put All Sections On Alert", Uniqueness.UNIQUE);
        setLore("'We have an emergency alert in detention block AA-twenty three.'");
        setGameText("USED: Target a Rebel at a Death Star (or Executor) site. For remainder of turn, target may not use its game text and may not apply ability toward drawing battle destiny. (Immune to Sense.) LOST: Retrieve into hand up to two cards with 'Death Star' in title.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter targetFilter = Filters.and(Filters.Rebel, Filters.at(Filters.or(Filters.Death_Star_site, Filters.Executor_site)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            action.setText("Target a Rebel");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Rebel", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard starship) {
                            action.addAnimationGroup(starship);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(starship) + "'s game text and prevent target from applying ability toward drawing battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
        action.setText("Retrieve cards with 'Death Star' in title");
        // Allow response(s)
        action.allowResponses("Retrieve up to two cards with 'Death Star' in title",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new RetrieveCardsIntoHandEffect(action, playerId, 2, true, false, Filters.titleContains("Death Star")));
                    }
                }
        );
        actions.add(action);

        return actions;
    }
}