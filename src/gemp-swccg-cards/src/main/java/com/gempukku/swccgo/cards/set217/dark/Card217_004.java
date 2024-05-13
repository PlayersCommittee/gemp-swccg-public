package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 1 (V)
 */
public class Card217_004 extends AbstractCombatVehicle {
    public Card217_004() {
        super(Side.DARK, 2, 6, 7, 7, null, 1, 7, Title.Blizzard_1, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("General Veers' AT-AT. Enclosed. Equipped with highly sophisticated communications gear. Employs an experimental targeting system.");
        setGameText("May add 2 pilots and 8 passengers. While Veers piloting: armor +1, draws one battle destiny if unable to otherwise, and targets Blizzard 1 'hits' here are forfeit = 0. Immune to Under Attack and attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(2);
        setPassengerCapacity(8);
        addPersona(Persona.BLIZZARD_1);
        setMatchingPilotFilter(Filters.Veers);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ArmorModifier(self, self, new HasPilotingCondition(self, Filters.Veers), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new HasPilotingCondition(self, Filters.Veers), 1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Under_Attack));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // targets Blizzard 1 hits here are forfeit = 0

        // Check condition(s)
        if (GameConditions.hasPiloting(game, self, Filters.Veers)
                && TriggerConditions.justHitBy(game, effectResult, Filters.here(self), self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reset " + GameUtils.getFullName(cardHit) + "'s forfeit to 0");
            // Perform result(s)
            action.appendEffect(
                    new ResetForfeitEffect(action, cardHit, 0));
            return Collections.singletonList(action);
        }
        return null;
    }
}