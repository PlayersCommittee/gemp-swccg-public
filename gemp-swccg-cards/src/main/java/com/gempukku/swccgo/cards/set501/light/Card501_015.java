package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Interrupt
 * Subtype: Used
 * Title: Our Only Hope (V)
 */
public class Card501_015 extends AbstractUsedInterrupt {
    public Card501_015() {
        super(Side.LIGHT, 4, Title.Our_Only_Hope, Uniqueness.UNIQUE);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("If He Is The Chosen One or He Will Bring Balance on table, take [Endor] Leia, Yoda's Hut, or a Death Star II site into hand from Reserve Deck; reshuffle. OR If a battle just initiated at same site as Prophecy Of The Force, place a card from hand in Reserve Deck; reshuffle.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_14);
        setVirtualSuffix(true);
        setTestingText("Our Only Hope (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OUR_ONLY_HOPE__UPLOAD_LEIA_OR_LOCATION;

        if (GameConditions.canSpot(game, self, Filters.or(Filters.He_Is_The_Chosen_One, Filters.He_Will_Bring_Balance))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take [Endor] Leia, Yoda's Hut, or a Death Star II site into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Icon.ENDOR, Filters.Leia), Filters.Yodas_Hut, Filters.Death_Star_II_site), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.hasAttached(Filters.Prophecy_Of_The_Force))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place card from hand on Reserve Deck");
                action.setActionMsg("Place card from hand on Reserve Deck");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new PutCardFromHandOnReserveDeckEffect(action, playerId));
                        action.appendEffect(new ShuffleReserveDeckEffect(action, playerId));
                    }
                });
                return Collections.singletonList(action);
        }
        return null;
    }
}