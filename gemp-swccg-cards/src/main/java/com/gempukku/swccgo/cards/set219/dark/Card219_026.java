package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Imperial
 * Title: Vader (V)
 */
public class Card219_026 extends AbstractImperial {
    public Card219_026() {
        super(Side.DARK, 1, 6, 6, 6, 8, Title.Vader, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Sought to extinguish all Jedi. Former student of Obi-Wan Kenobi. Seduced by the dark side of the Force.");
        setGameText("[Pilot] 3. If alone and a battle was just initiated here, may [upload] Lightsaber Parry, Physical Choke, or Vader's Anger. " +
                    "Opponent's non-Jedi characters here are power and forfeit -1. Immune to attrition < 5 (< 7 if with a Jedi).");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.non_Jedi_character, Filters.here(self)), -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.non_Jedi_character, Filters.here(self)), -1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 7, new WithCondition(self, Filters.Jedi))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.VADER__UPLOAD_CARD;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isAlone(game, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Lightsaber Parry, Physical Choke, or Vader's Anger into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title("Lightsaber Parry"), Filters.title("Physical Choke"), Filters.title("Vader's Anger")), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
