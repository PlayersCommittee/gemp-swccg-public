package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: There They Are!
 */
public class Card14_107 extends AbstractUsedInterrupt {
    public Card14_107() {
        super(Side.DARK, 5, Title.There_They_Are, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("When unexplained droid losses are detected, a Droid Control Ship can call in reinforcements in seconds.");
        setGameText("If a battle was just initiated, your non-unique battle droids are each power +1 for remainder of battle. (Immune to Sense if battle at a Naboo site.) OR Take a non-unique battle droid or non-unique droid starfighter into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.non_unique, Filters.battle_droid, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add power to non-unique battle droids");
            if (GameConditions.isDuringBattleAt(game, Filters.Naboo_site)) {
                action.setImmuneTo(Title.Sense);
            }
            // Allow response(s)
            action.allowResponses("Make non-unique battle droids power +1",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> battleDroids = Filters.filterActive(game, self, filter);
                            if (!battleDroids.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(battleDroids), 1),
                                                "Makes " + GameUtils.getAppendedNames(battleDroids) + " power +1"));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.THERE_THEY_ARE__UPLOAD_NON_UNIQUE_BATTLE_DROID_OR_DROID_STARFIGHTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a non-unique battle droid or non-unique droid starfighter into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.or(Filters.battle_droid, Filters.droid_starfighter)), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}