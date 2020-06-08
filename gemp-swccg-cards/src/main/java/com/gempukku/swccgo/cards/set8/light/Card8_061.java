package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Take The Initiative
 */
public class Card8_061 extends AbstractUsedOrLostInterrupt {
    public Card8_061() {
        super(Side.LIGHT, 3, "Take The Initiative", Uniqueness.UNIQUE);
        setLore("The ability to think and act independently gave the Rebels an advantage over their Imperial foes.");
        setGameText("USED: If all of your ability in a battle is provided by scouts and/or spies, they each add 1 to your total battle destiny (limit +6). LOST: For remainder of turn, your unique (•) scouts and unique (•) spies are each power +1 (or +2 while being attacked by a creature).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isAllAbilityInBattleProvidedBy(game, playerId, Filters.or(Filters.scout, Filters.spy))) {
            final Filter filter2 = Filters.and(Filters.your(self), Filters.or(Filters.scout, Filters.spy), Filters.participatingInBattle);
            int count = Math.min(6, Filters.countActive(game, self, filter2));
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Add " + count + " to total battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                final int numToAdd = Math.min(6, Filters.countActive(game, self, filter2));
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, numToAdd));
                            }
                        }
                );
                actions.add(action);
            }
        }

        final Filter filter = Filters.and(Filters.your(self), Filters.unique, Filters.or(Filters.scout, Filters.spy));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add to power of scouts and spies");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new PowerModifier(self, filter, new CardMatchesEvaluator(1, 2, Filters.beingAttackedByCreature)),
                                            "Makes unique scouts and unique spies power +1 (or +2 while being attacked by a creature)"));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}