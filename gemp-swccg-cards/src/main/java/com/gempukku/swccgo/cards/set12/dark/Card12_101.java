package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Sith
 * Title: Darth Maul, Young Apprentice
 */
public class Card12_101 extends AbstractSith {
    public Card12_101() {
        super(Side.DARK, 1, 7, 7, 6, 8, "Darth Maul, Young Apprentice", Uniqueness.UNIQUE);
        setLore("Fueled by a hatred of the Jedi and an arsenal of dark abilities, this Sith warrior is a powerful weapon for his dark mentor, Darth Sidious.");
        setGameText("Deploys -2 to Coruscant. When Maul swings a lightsaber at a Jedi, each weapon destiny draw is +1. If Maul hits a Jedi Master during battle, that Jedi Master is power -3 for remainder of battle. Immune to Clash Of Sabers and attrition < 5.");
        addPersona(Persona.MAUL);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Coruscant));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.lightsaber, Filters.Jedi));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Jedi_Master, self)
                && GameConditions.isDuringBattle(game)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce " + GameUtils.getFullName(cardHit) + "'s power by 3");
            action.setActionMsg("Reduce " + GameUtils.getCardLink(cardHit) + "'s power by 3");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfBattleEffect(action, cardHit, -3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
