package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Used
 * Title: Orbital Bombardment
 */
public class Card225_026 extends AbstractUsedInterrupt {
    public Card225_026() {
        super(Side.DARK, 5, "Orbital Bombardment", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("During battle at a site, if your Dreadnaught or Star Destroyer occupies the related system, your total battle destiny is +1. If you control the system or Fulminatrix there, +2 instead and the number of battle destiny draws may not be limited.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {

        Filter systemRelatedToBattle = Filters.relatedSystemTo(self, Filters.battleLocation);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.occupiesWith(game, self, playerId, systemRelatedToBattle, Filters.or(Filters.Dreadnaught, Filters.Star_Destroyer))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);

            // Check more condition(s)
            if (GameConditions.controls(game, playerId, systemRelatedToBattle)
                    || GameConditions.occupiesWith(game, self, playerId, systemRelatedToBattle, Filters.Fulminatrix)) {
                
                action.setText("Add 2 to total battle destiny and prevent limits");
                // Allow response(s)
                action.allowResponses("Add 2 to total battle destiny and prevent the number of battle destiny draws from being limited",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                    new ModifyTotalBattleDestinyEffect(action, playerId, 2));
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier(self, Filters.battleLocation),
                                                "Prevents the number of battle destiny draws from being limited"));
                            }
                        });
            }
            else {
                action.setText("Add 1 to total battle destiny");
                // Allow response(s)
                action.allowResponses("Add 1 to total battle destiny",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                    new ModifyTotalBattleDestinyEffect(action, playerId, 1));
                            }
                        });
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}