package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerXwingEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerYwingEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Organized Attack
 */
public class Card7_097 extends AbstractUsedOrLostInterrupt {
    public Card7_097() {
        super(Side.LIGHT, 5, Title.Organized_Attack);
        setLore("'Hold up here and wait for my signal to start your run.'");
        setGameText("USED: If a battle was just initiated, each of your X-wings and Y-wings present is power +1 and immune to attrition for remainder of turn. LOST: Lose 1 Force to take up to three non-unique X-wings and/or non-unique Y-wings into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.X_wing, Filters.Y_wing), Filters.piloted, Filters.presentInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add power and immunity to X-wings and Y-wings");
            // Allow response(s)
            action.allowResponses("Make X-wings and Y-wings present power +1 and immune to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> xWingsAndYwings = Filters.filterActive(game, self, filter);
                            if (!xWingsAndYwings.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(xWingsAndYwings), new AddEvaluator(new PerXwingEvaluator(1), new PerYwingEvaluator(1))),
                                                "Makes " + GameUtils.getAppendedNames(xWingsAndYwings) + " power +1"));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ImmuneToAttritionModifier(self, Filters.in(xWingsAndYwings)),
                                                "Makes " + GameUtils.getAppendedNames(xWingsAndYwings) + " immune to attrition"));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ORGANIZED_ATTACK__UPLOAD_NON_UNIQUE_XWINGS_AND_YWINGS;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take cards into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Take up to three non-unique X-wings and/or non-unique Y-wings into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 3, Filters.and(Filters.non_unique, Filters.or(Filters.X_wing, Filters.Y_wing)), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}