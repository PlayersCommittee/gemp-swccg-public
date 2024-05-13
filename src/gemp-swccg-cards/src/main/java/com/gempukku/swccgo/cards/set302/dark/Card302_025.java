package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.TotalAbilityLessThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Imperial
 * Title: Dacien Victae, Deputy Grand Master
 */
public class Card302_025 extends AbstractDarkJediMasterImperial {
    public Card302_025() {
        super(Side.DARK, 6, 6, 5, 7, 8, "Dacien Victae, Deputy Grand Master", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("An aged leader of the Brotherhood. Dacien has withstood several invasion of their territories. Currently serving as the Deputy Grand Master under Evant though his aspirations for the throne are not easily masked.");
        setGameText("Adds 3 to anything he pilots. If at the same site as Evant may add two Destinies for a battle. Total ability of 6 of more required for opponent to draw destiny here. Immune to attrition < 5.");
        addPersona(Persona.BUBBA);
		addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.DARK_COUNCILOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
		String opponent = game.getOpponent(self.getOwner());
		
		List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
		modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Evant), 2));
		modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, Filters.sameSite(self),
			new TotalAbilityLessThanCondition(opponent, 6, Filters.sameSite(self)), opponent));
        return modifiers;
    }
	
}
