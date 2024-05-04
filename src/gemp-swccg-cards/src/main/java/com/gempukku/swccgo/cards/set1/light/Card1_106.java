package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rebel Reinforcements
 */
public class Card1_106 extends AbstractLostInterrupt {
    public Card1_106() {
        super(Side.LIGHT, 4, Title.Rebel_Reinforcements, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("Rebels rely on hidden 'cells' of undercover operatives. Striking from hidden bases, troops or starfighters can arrive in a battle zone at any time.");
        setGameText("If opponent outnumbers you in total characters and starships on table, use 1 Force to draw destiny. That number of Rebel Troopers and/or Y-Wings, may be retrieved from Lost Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && (Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship))) <
                Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny to retrieve cards");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Draw destiny to retrieve Rebel Troopers and/or Y-Wings",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null && totalDestiny > 0) {
                                                action.appendEffect(
                                                        new RetrieveCardsEffect(action, playerId, totalDestiny, Filters.or(Filters.and(Filters.Rebel, Filters.trooper), Filters.Y_wing)));
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}