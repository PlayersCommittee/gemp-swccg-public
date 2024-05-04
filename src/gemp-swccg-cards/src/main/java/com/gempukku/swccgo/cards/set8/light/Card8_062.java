package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: This Is Absolutely Right
 */
public class Card8_062 extends AbstractUsedInterrupt {
    public Card8_062() {
        super(Side.LIGHT, 5, "This Is Absolutely Right", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("'He's my brother.'");
        setGameText("If Han and Leia are present together at a battleground site (and neither is Undercover), choose one of the following: Your Force drain is +1 at that site this turn. OR Opponent's Force drains at related locations are -1 next turn. OR Cancel This Is Still Wrong.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, self)) {
            final PhysicalCard site = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.battleground_site,
                    Filters.and(Filters.wherePresent(self, Filters.and(Filters.Han, Filters.not(Filters.undercover_spy))), Filters.wherePresent(self, Filters.and(Filters.Leia, Filters.not(Filters.undercover_spy))))));
            if (site != null) {

                final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
                action1.setText("Make Force drains +1 this turn");
                // Allow response(s)
                action1.allowResponses("Make Force drains at " + GameUtils.getCardLink(site) + " +1 this turn",
                        new RespondablePlayCardEffect(action1) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action1.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action1,
                                                new ForceDrainModifier(self, Filters.sameLocationId(site), 1, playerId),
                                                "Makes Force drains at " + GameUtils.getCardLink(site) + " +1 this turn"));
                            }
                        }
                );
                actions.add(action1);

                if (GameConditions.canSpotLocation(game, Filters.relatedLocationTo(self, Filters.sameLocationId(site)))) {

                    final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
                    action2.setText("Make Force drains -1 next turn");
                    // Allow response(s)
                    action2.allowResponses("Make Force drains at locations related to " + GameUtils.getCardLink(site) + " -1 next turn",
                            new RespondablePlayCardEffect(action2) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action2.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action2,
                                                    new ForceDrainModifier(self, Filters.relatedLocationTo(self, Filters.sameLocationId(site)), -1, opponent),
                                                    "Makes Force drains at locations related to " + GameUtils.getCardLink(site) + " -1 next turn"));
                                }
                            }
                    );
                    actions.add(action2);
                }
            }
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.This_Is_Still_Wrong)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.This_Is_Still_Wrong, Title.This_Is_Still_Wrong);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.This_Is_Still_Wrong)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}