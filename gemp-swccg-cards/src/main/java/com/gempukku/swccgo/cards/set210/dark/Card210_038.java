package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Vehicle
 * Subtype: Combat
 * Title: Marquand in Blizzard 6
 */
public class Card210_038 extends AbstractCombatVehicle {
    public Card210_038() {
        super(Side.DARK, 1, 6, 6, 7, null, 1, 6, "Marquand in Blizzard 6", Uniqueness.UNIQUE);
        setLore("Enclosed. Death Squadron.");
        setGameText("May add 1 pilot. Permanent pilot is •Marquand, who provides ability of 2. Cards Blizzard 6 hits are power and forfeit -2 and may not apply ability towards drawing battle destiny. Immune to attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.VIRTUAL_SET_10, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        //Permanent pilot is •Marquand, who provides ability of 2
        return Collections.singletonList(new AbstractPermanentPilot(Persona.MARQUAND, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // Immune to attrition < 4.
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // Cards Blizzard 6 hits are power and forfeit -2 and may not apply ability towards drawing battle destiny.

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " power and forfeit - 2.");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardHit) + " power and forfeit - 2.");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerEffect(action, cardHit, -2));
            action.appendEffect(
                    new ModifyForfeitEffect(action, cardHit, -2));
            action.appendEffect(
                    new MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(action, cardHit));
            return Collections.singletonList(action);
        }
        return null;
    }
}
