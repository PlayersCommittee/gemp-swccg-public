package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Darth Vader
 */
public class Card1_168 extends AbstractImperial {
    public Card1_168() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Darth Vader", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Dark Lord of the Sith. Servant of Emperor's. Encased in armor with cybernetic life support. Student of Obi-Wan Kenobi. Was the best starpilot in the galaxy. Cunning warrior.");
        setGameText("When in battle, adds 1 to each of your battle destiny draws. Adds 3 to power of anything he pilots (or 4 to power and 3 to maneuver if Vader's Custom TIE). Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.BLACK_SQUADRON);
        setMatchingStarshipFilter(Filters.Vaders_Custom_TIE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self),
                new NotCondition(new GameTextModificationCondition(self, ModifyGameTextType.VADER__DOES_NOT_ADD_1_TO_BATTLE_DESTINY)),
                1, self.getOwner()));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(3, 4, Filters.Vaders_Custom_TIE)));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Vaders_Custom_TIE, Filters.hasPiloting(self)), 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
