package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLostPileToWeatherVane;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Fall Of The Legend
 */
public class Card5_047 extends AbstractUsedInterrupt {
    public Card5_047() {
        super(Side.LIGHT, 6, "Fall Of The Legend", Uniqueness.UNIQUE);
        setLore("Luke had a falling out with his estranged father.");
        setGameText("If you just lost a character during a battle or duel at a Cloud City site, use 2 Force to relocate that character to Weather Vane instead of your Lost Pile. OR Search your Reserve Deck, take one Weather Vane into hand and reshuffle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, playerId, Filters.character)
                && (GameConditions.isDuringBattleAt(game, Filters.Cloud_City_site) || GameConditions.isDuringDuelAt(game, Filters.Cloud_City_site))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canSpot(game, self, Filters.Weather_Vane)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate " + GameUtils.getFullName(justLostCard) + " to Weather Vane");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Relocate " + GameUtils.getCardLink(justLostCard) + " to Weather Vane",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RelocateFromLostPileToWeatherVane(action, justLostCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.FALL_OF_THE_LEGEND__UPLOAD_WEATHER_VANE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Weather Vane into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Weather_Vane, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}