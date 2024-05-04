package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Gungan Warrior
 */
public class Card12_007 extends AbstractAlien {
    public Card12_007() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Gungan Warrior", Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.C);
        setLore("Residing in the underwater city of Otoh Gunga, the Gungans established an alliance with the Naboo to fight the Trade Federation's forceful occupation of their planet.");
        setGameText("Deploys -1 to a Naboo site opponent occupies. While with another Gungan, draws one battle destiny if unable to otherwise. Power +3 while armed with an electropole.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        setSpecies(Species.GUNGAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.and(Filters.Naboo_site, Filters.occupies(game.getOpponent(self.getOwner())))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new WithCondition(self, Filters.Gungan), 1));
        modifiers.add(new PowerModifier(self, new ArmedWithCondition(self, Filters.electropole), 3));
        return modifiers;
    }
}
