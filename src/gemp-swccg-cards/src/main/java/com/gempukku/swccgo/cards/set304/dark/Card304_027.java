package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Rohan Lap'lamiz, Stormtrooper
 */
public class Card304_027 extends AbstractImperial {
    public Card304_027() {
        super(Side.DARK, 3, 4, 3, 3, 5, "Rohan Lap'lamiz, Stormtrooper", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Kamjin, favoring his second born's love of Stormtrooper armor, had a custom suit created for him. To the dismay of his advisors he further appointed him an officer within the Imperial Legion.");
        setGameText("Adds one battle destiny with a stormtrooper. If you just won a battle at same or related location, may retrieve a trooper. Adds 3 to forfeit of each stormtrooper of ability < 3 at same site.");
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.CSP);
        addKeywords(Keyword.STORMTROOPER, Keyword.MALE);
		addPersona(Persona.ROHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
		modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.stormtrooper), 1));
		modifiers.add(new ForfeitModifier(self, Filters.and(Filters.stormtrooper, Filters.abilityLessThan(3), Filters.atSameSite(self)), 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.sameOrRelatedLocation(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve a trooper");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.trooper));
            return Collections.singletonList(action);
        }
        return null;
    }
}
