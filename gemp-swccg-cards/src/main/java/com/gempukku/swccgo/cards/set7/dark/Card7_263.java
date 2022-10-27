package com.gempukku.swccgo.cards.set7.dark;

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
 * Title: Surface Defense
 */
public class Card7_263 extends AbstractUsedOrStartingInterrupt {
    public Card7_263() {
        super(Side.DARK, 5, "Surface Defense", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("'The battle station is heavily shielded and carries a firepower greater than half the starfleet.'");
        setGameText("USED: Add 1 to your just-drawn weapon destiny. STARTING: If you have deployed a battleground, deploy Resistance, Secret Plans and/or There Is No Try from Reserve Deck. Place Interrupt in Reserve Deck.");
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
            action.allowResponses("Deploy Resistance, Secret Plans, and/or There Is No Try from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.or(Filters.Resistance, Filters.Secret_Plans, Filters.There_Is_No_Try), 1, 3, true, false));
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