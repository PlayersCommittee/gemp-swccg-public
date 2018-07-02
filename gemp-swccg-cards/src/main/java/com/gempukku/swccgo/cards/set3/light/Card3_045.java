package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
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
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Lucky Shot
 */
public class Card3_045 extends AbstractUsedOrLostInterrupt {
    public Card3_045() {
        super(Side.LIGHT, 4, "Lucky Shot");
        setLore("AT-ATs are nearly impervious to infantry fire, but occasionally a blaster bolt hits between the plating. It's one shot in a million, but such odds are a familiar to the Rebellion.");
        setGameText("Add X to one weapon destiny (before weapon destiny is drawn) when targeting a combat vehicle. USED: X = 1. LOST: X = 3.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.combat_vehicle);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawWeaponDestinyTargeting(game, effectResult, Filters.weapon, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 1 to weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyAboutToBeDrawnEffect(action, 1));
                        }
                    }
            );
            actions.add(action);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action2.setText("Add 3 to weapon destiny");
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ModifyDestinyAboutToBeDrawnEffect(action2, 3));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}