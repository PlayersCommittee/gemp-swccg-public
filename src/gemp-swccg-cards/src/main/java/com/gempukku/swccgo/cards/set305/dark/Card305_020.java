package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractMandalorian;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.PerCSPEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerVizslaEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Mandalorian
 * Title: Declan Roark
 */

public class Card305_020 extends AbstractMandalorian {
    public Card305_020() {
        super(Side.DARK, 1, 6, 6, 4, 8, "Declan Roark", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setArmor(7);
        setLore("A Mandalorian warrior who rose to be the leader of Clan Vizsla. He began his life as a mercenary but never loss his sense of compassion for his fellow Mandalorians.");
        setGameText("Adds 2 to power of anything he pilots. Adds 3 to power, 4 to defense value, and 4 to forfeit of each [Vizsla] character at same and related locations. May be targeted by Hidden Weapons. Immune to attrition.");
        addPersona(Persona.DECLAN);
        setSpecies(Species.MANDALORIAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIZSLA);
        addKeywords(Keyword.MERCENARY, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter withVizsla = Filters.and(Filters.your(self), Filters.VIZSLA_character, Filters.atSameOrRelatedLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Hidden_Weapons));
        modifiers.add(new PowerModifier(self, withVizsla, new PerVizslaEvaluator(3)));
        modifiers.add(new ForfeitModifier(self, withVizsla, new PerVizslaEvaluator(4)));
        modifiers.add(new DefenseValueModifier(self, withVizsla, new PerVizslaEvaluator(4)));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}


