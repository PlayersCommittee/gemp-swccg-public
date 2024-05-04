package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: All Power To Weapons
 */
public class Card7_246 extends AbstractUsedOrLostInterrupt {
    public Card7_246() {
        super(Side.DARK, 4, "All Power To Weapons", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("The TIE fighter's energy management system allows its pilot to transfer all power from engines to weapons at a moment's notice.");
        setGameText("USED: If a battle was just initiated, each of your TIEs present is power +2 and immune to attrition for remainder of turn. LOST: Lose 1 Force to take up to three non-unique TIEs into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.TIE, Filters.piloted, Filters.presentInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add power and immunity to TIEs");
            // Allow response(s)
            action.allowResponses("Make TIEs present power +2 and immune to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> ties = Filters.filterActive(game, self, filter);
                            if (!ties.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(ties), new PerTIEEvaluator(2)),
                                                "Makes " + GameUtils.getAppendedNames(ties) + " power +2"));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ImmuneToAttritionModifier(self, Filters.in(ties)),
                                                "Makes " + GameUtils.getAppendedNames(ties) + " immune to attrition"));
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
        GameTextActionId gameTextActionId = GameTextActionId.ALL_POWER_TO_WEAPONS__UPLOAD_NON_UNIQUE_TIES;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take TIEs into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Take up to three non-unique TIEs into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 3, Filters.and(Filters.non_unique, Filters.TIE), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}