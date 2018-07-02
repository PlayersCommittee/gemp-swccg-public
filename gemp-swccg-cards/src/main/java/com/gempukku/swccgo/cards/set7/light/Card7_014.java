package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Debnoli
 */
public class Card7_014 extends AbstractAlien {
    public Card7_014() {
        super(Side.LIGHT, 2, 3, 3, 2, 4, "Debnoli", Uniqueness.UNIQUE);
        setLore("Good-natured patron of Mos Eisley cantina, until the Empire impounded his starship. Expert marksman. Seeking revenge on the Empire.");
        setGameText("Adds 2 to power of anything he pilots. When firing a character weapon, adds 1 to total weapon destiny and characters he 'hits' are forfeit=0. Immune to attrition < 2 (< 5 while armed with a blaster).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.character_weapon));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(2, 5, new ArmedWithCondition(self, Filters.blaster))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.character_weapon, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reset " + GameUtils.getFullName(cardHit) + "'s forfeit to 0");
            action.setActionMsg("Reset " + GameUtils.getCardLink(cardHit) + "'s forfeit to 0");
            // Perform result(s)
            action.appendEffect(
                    new ResetForfeitEffect(action, cardHit, 0));
            return Collections.singletonList(action);
        }
        return null;
    }
}
