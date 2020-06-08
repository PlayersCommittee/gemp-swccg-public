package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Desperate Tactics
 */
public class Card7_086 extends AbstractUsedInterrupt {
    public Card7_086() {
        super(Side.LIGHT, 4, "Desperate Tactics", Uniqueness.UNIQUE);
        setLore("Members of the Rebel Alliance are willing to make a perilous frontal attack for their cause.");
        setGameText("If your vehicle weapon was just fired during a battle, add one battle destiny. OR During a battle at a site where you have an artillery weapon or vehicle weapon, cancel all opponent's immunity to attrition for remainder of battle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFired(game, effectResult, Filters.and(Filters.your(self), Filters.vehicle_weapon))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

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
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.artillery_weapon, Filters.vehicle_weapon))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel immunity to attrition");
            // Allow response(s)
            action.allowResponses("Cancel all opponent's immunity to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new CancelImmunityToAttritionModifier(self, Filters.and(Filters.opponents(self), Filters.participatingInBattle)),
                                            "Cancels all opponent's immunity to attrition"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}