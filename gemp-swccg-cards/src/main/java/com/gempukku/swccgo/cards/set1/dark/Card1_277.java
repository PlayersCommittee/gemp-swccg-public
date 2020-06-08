package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Vader's Eye
 */
public class Card1_277 extends AbstractLostInterrupt {
    public Card1_277() {
        super(Side.DARK, 4, "Vader's Eye", Uniqueness.UNIQUE);
        setLore("Darth Vader's armored mask and life-support system provide him with extraordinary physical protection in duels and battles, in addition to his Dark Jedi combat skill.");
        setGameText("If Vader is defending a battle alone at a site, add 1 to power and add one battle destiny. OR If any other Imperial with ability > 2 is defending a battle alone at a site, add one battle destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Vader, Filters.defendingBattle, Filters.alone))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add 1 to power and add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 1, playerId, "Adds 1 to total power"));
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
            else if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Imperial, Filters.abilityMoreThan(2), Filters.defendingBattle, Filters.alone))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}