package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ResetAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Lost
 * Title: One Beautiful Thing
 */
public class Card601_092 extends AbstractLostInterrupt {
    public Card601_092() {
        super(Side.DARK, 4, "One Beautiful Thing", Uniqueness.UNIQUE);
        setLore("'I don't know how we're gonna get out of this one.'");
        setGameText("Lose 1 Force to take Juno Eclipse into hand from Reserve Deck (or retrieve); reshuffle. OR During battle where you have no Dark Jedi or droids and only two participating cards with ability (a male and a female), add one destiny to total power (and attrition against you = 0).");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ONE_BEAUTIFUL_THING__PULL_JUNO;

        if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Juno into hand from Reserve Deck");
            action.setActionMsg("Take Juno Eclipse into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take Juno Eclipse into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Juno, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve Juno");
            action.setActionMsg("Retrieve Juno Eclipse");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Juno));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)
                && !GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.Dark_Jedi, Filters.droid)))) {
            Collection<PhysicalCard> cardsWithAbilityInBattle = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.participatingInBattle, Filters.hasAbility));
            if (cardsWithAbilityInBattle.size() == 2
                    && !Filters.filter(cardsWithAbilityInBattle, game, Filters.male).isEmpty()
                    && !Filters.filter(cardsWithAbilityInBattle, game, Filters.female).isEmpty()
            ) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Add destiny to power and reset attrition");
                action.setActionMsg("Add one destiny to power and make attrition against you = 0");
                // Pay cost(s)
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                                    action.appendEffect(
                                            new AddDestinyToTotalPowerEffect(action, 1));
                                }
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new ResetAttritionModifier(self, Filters.battleLocation, 0, playerId),
                                                "Make attrition against you = 0")
                                );
                            }
                        }
                );

                actions.add(action);
            }
        }
        return actions;
    }
}