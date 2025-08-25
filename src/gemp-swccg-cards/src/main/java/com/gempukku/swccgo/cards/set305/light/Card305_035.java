package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Mihoshi Keibatsu
 */
public class Card305_035 extends AbstractAlien {
    public Card305_035() {
        super(Side.LIGHT, 2, 6, 5, 6, 7, "Mihoshi Keibatsu", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("Currently a leader in clan Odan-Urr. This Odanite loves the cards and the dice almost as much as she does fighting. As a gambler the only god she worships is luck.");
        setGameText("Deploys -3 to Quermia. Mihoshi and your other [COU] characters present are defense value +2, and immune to You Are Beaten, Sniper, and attrition < 3.");
        addPersona(Persona.MIHOSHI);
        addIcons(Icon.ABT, Icon.COU, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.GAMBLER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.Deploys_at_Quermia));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter mihoshiAndOtherCharacters = Filters.or(self, Filters.and(Filters.your(self), Filters.other(self),
                Filters.COU, Filters.character, Filters.present(self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, mihoshiAndOtherCharacters, 2));
        modifiers.add(new ImmuneToTitleModifier(self, mihoshiAndOtherCharacters, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToTitleModifier(self, mihoshiAndOtherCharacters, Title.Sniper));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, mihoshiAndOtherCharacters, 3));
        return modifiers;
    }
}
