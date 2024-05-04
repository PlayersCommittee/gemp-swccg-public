package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Don't Tread On Me
 */
public class Card7_088 extends AbstractUsedOrStartingInterrupt {
    public Card7_088() {
        super(Side.LIGHT, 5, "Don't Tread On Me", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Han did not take kindly to Jabba's posturing.");
        setGameText("USED: Add 1 to your just-drawn weapon destiny. STARTING: If you have deployed a battleground, deploy Ultimatum, Scrambled Transmission and/or Do, Or Do Not from Reserve Deck. Place Interrupt in Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 1 to weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.battleground);

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, filter)
                || GameConditions.canSpotConvertedLocation(game, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy cards from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Ultimatum, Scrambled Transmission, and/or Do, Or Do Not from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.or(Filters.Ultimatum, Filters.Scrambled_Transmission, Filters.Do_Or_Do_Not), 1, 3, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}