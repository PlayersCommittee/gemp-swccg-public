package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Sith
 * Title: Darth Vader, Betrayer Of The Jedi
 */
public class Card216_006 extends AbstractSith {
    public Card216_006() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Darth Vader, Betrayer Of The Jedi", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("[Pilot] 3. During battle, if a lightsaber swung by Vader just 'hit' a character, character's game text is canceled (and if Amidala, she is immediately lost). While on Coruscant, adds one [Dark Side] icon here. Immune to [Set 3] Amidala and attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new IconModifier(self, Filters.and(Filters.location, Filters.here(self)), new OnCondition(self, Title.Coruscant), Icon.DARK_FORCE));
        //immune to [Set 3] Padme. made him immune to Padme Naberrie and made her check if Vader is immune
        modifiers.add(new ImmuneToTitleModifier(self, Title.Padme));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.lightsaber, Filters.Vader)) {
            PhysicalCard character = ((HitResult) effectResult).getCardHit();
            if (Filters.character.accepts(game, character)) {
                boolean isAmidala = Filters.Amidala.accepts(game, character);

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Character's game text canceled");
                if (isAmidala)
                    action.setActionMsg("Cancel " + GameUtils.getCardLink(character) + "'s game text and make her lost");
                else
                    action.setActionMsg("Cancel " + GameUtils.getCardLink(character) + "'s game text");

                // Perform result(s)
                action.appendEffect(new CancelGameTextEffect(action, character));
                if (isAmidala) {
                    action.appendEffect(new LoseCardFromTableEffect(action, character));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
