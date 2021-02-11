package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Hear Me Baby, Hold Together (V)
 */
public class Card601_045 extends AbstractUsedInterrupt {
    public Card601_045() {
        super(Side.LIGHT, 5, "Hear Me Baby, Hold Together");
        setVirtualSuffix(true);
        setLore("Smuggler and Rebel starships use black market armor plating and deflector shields to withstand enemy fire. Expensive but life-saving modifications.");
        setGameText("Take a 'grabber' into hand from Reserve Deck; reshuffle. (Immune to Sense.)  OR  Play a Defensive Shield from under your Starting Effect.  OR  Cancel Counter Assault, [Virtual Block 2] Defensive Fire, Hidden Weapons, I'd Just As Soon Kiss A Wookiee, or Overload.  OR  Lose 1 Force to cancel Cease Fire (except during a battle at a [Reflections III] site).");
        addIcons(Icon.BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HEAR_ME_BABY_HOLD_TOGETHER__UPLOAD_GRABBER;
        if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take grabber into hand from Reserve Deck");
            action.setImmuneTo(Title.Sense);
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    // Perform result(s)
                    action.appendEffect(
                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.grabber, true));
                }
            });
            actions.add(action);
        }


        // Check condition(s)
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Play a Defensive Shield");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Counter_Assault, Filters.and(Filters.Defensive_Fire, Icon.BLOCK_2), Filters.Hidden_Weapons, Filters.Id_Just_As_Soon_Kiss_A_Wookiee, Filters.Overload))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Cease_Fire)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && !GameConditions.isDuringBattleAt(game, Filters.and(Icon.REFLECTIONS_III, Filters.site))) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            actions.add(action);
        }

        return actions;
    }
}