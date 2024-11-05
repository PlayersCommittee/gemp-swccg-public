package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.PerCSPEvaluator;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Kamjin Lap'lamiz, The Conqueror
 */
public class Card304_029 extends AbstractDarkJediMasterImperial {
    public Card304_029() {
        super(Side.DARK, 6, 6, 4, 7, 9, "Kamjin Lap'lamiz, The Conqueror", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Under Kamjin's rule, Scholae Palatinae finally brought all of the Caperion system under its rule. The Scholae Palatinae Legion began to chant 'Conqueror' when he appeared as a result of his victories.");
		setGameText("Adds +3 to anything he pilots. Adds 3 to power, 4 to defense value, and 4 to forfeit of each [CSP Icon] member at same and related locations. May be targeted by Force Lightning. Immune to attrition.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.CSP, Icon.WARRIOR, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.CSP_EMPEROR, Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter withCSP = Filters.and(Filters.your(self), Filters.CSP, Filters.atSameOrRelatedLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
		modifiers.add(new MayBeTargetedByModifier(self, Title.Force_Lightning));
        modifiers.add(new ImmuneToAttritionModifier(self));
        modifiers.add(new PowerModifier(self, withCSP, new PerCSPEvaluator(3)));
        modifiers.add(new ForfeitModifier(self, withCSP, new PerCSPEvaluator(4)));
		modifiers.add(new DefenseValueModifier(self, withCSP, new PerCSPEvaluator(4)));

        return modifiers;
    }
}
