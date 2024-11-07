package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Great Behind Me
 */
public class Card304_115 extends AbstractUsedOrLostInterrupt {
    public Card304_115() {
        super(Side.LIGHT, 4, "Get Behind Me", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Get behind me!");
        setGameText("USED: Cancel an attempt to use a character weapon to target your character of ability < 4. LOST: Use 2 Force to cancel an attempt to use a [Permanent Weapon] weapon to target your character.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character, Filters.abilityLessThan(4)), Filters.character_weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character), Icon.PERMANENT_WEAPON)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel weapon targeting");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}