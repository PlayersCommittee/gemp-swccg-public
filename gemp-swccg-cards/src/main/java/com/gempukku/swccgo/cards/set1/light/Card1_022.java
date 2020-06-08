package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfPlayersNextTurnEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Owen Lars
 */
public class Card1_022 extends AbstractRebel {
    public Card1_022() {
        super(Side.LIGHT, 2, 2, 1, 3, 4, Title.Owen_Lars, Uniqueness.UNIQUE);
        setLore("Guardian of Luke Skywalker. Husband of Beru Lars. Brother of Obi-Wan Kenobi. Farmer of moisture. Disapprover of Luke's desire to leave Tatooine.");
        setGameText("Deploys free at Lars' Moisture Farm. Power +2 if at same site as Beru Lars or a Vaporator. If lost from table during opponent's turn, Luke is power +3 until the end of your next turn.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Lars_Moisture_Farm));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.or(Filters.Beru, Filters.Vaporator)), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.isOpponentsTurn(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make Luke power +3");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfPlayersNextTurnEffect(action, self.getOwner(), Filters.Luke, 3, "Makes Luke power +3"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
