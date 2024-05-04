package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
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
import com.gempukku.swccgo.logic.effects.ModifyDestinyAboutToBeDrawnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Steady Aim
 */
public class Card7_105 extends AbstractUsedOrLostInterrupt {
    public Card7_105() {
        super(Side.LIGHT, 3, "Steady Aim", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Although unfamiliar with the quad laser turrets on the Falcon, Luke quickly scored two hits.");
        setGameText("Add X to one starship weapon destiny (before destiny is drawn) when targeting opponent's starship. USED: X=2. LOST: X=4.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.starship);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawWeaponDestinyTargeting(game, effectResult, Filters.starship_weapon, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 2 to weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyAboutToBeDrawnEffect(action, 2));
                        }
                    }
            );
            actions.add(action);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action2.setText("Add 4 to weapon destiny");
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ModifyDestinyAboutToBeDrawnEffect(action2, 4));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}