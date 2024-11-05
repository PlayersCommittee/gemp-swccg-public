package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetCalculationVariableModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE/wi Modified Interceptor
 */
public class Card302_005 extends AbstractStarfighter {
    public Card302_005() {
        super(Side.DARK, 3, 2, 3, null, 4, 3, 4, "TIE/wi Modified Interceptor", Uniqueness.RESTRICTED_3, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Also known as the TIE Whisper was a First Order design modified to resemble the Empire's TIE Intercetpors. Since the Empire stealth packages and a hyperdrive have been included.");
        setGameText("May add 1 pilot. When firing SFS L-s9.3 Laser Cannons, may use 1 Force to make X = 3.");
		addIcons(Icon.NAV_COMPUTER);
        addModelType(ModelType.TIE_WI);
        setPilotCapacity(1);
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.SFS_Lx93_Laser_Cannons, self)
                && GameConditions.canUseForce(game, playerId, 1)) {
            PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();
            if (weapon != null) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make X = 3");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Perform result(s)
                action.appendEffect(
                        new AddUntilEndOfWeaponFiringModifierEffect(action, new ResetCalculationVariableModifier(self, weapon, 3, Variable.X), "Makes X = 3"));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
