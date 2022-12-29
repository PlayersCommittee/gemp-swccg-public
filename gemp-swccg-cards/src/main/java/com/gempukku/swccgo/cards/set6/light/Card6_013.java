package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Florn Lamproid
 */
public class Card6_013 extends AbstractAlien {
    public Card6_013() {
        super(Side.LIGHT, 3, 3, 1, 2, 2, "Florn Lamproid", Uniqueness.RESTRICTED_3, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Aggressive, serpent-like colonizers. Found on many jungle and forest planets. Have poison stinger that they use when defending themselves.");
        setGameText("May deploy as a 'react' to any jungle or forest site. Power and forfeit +2 while Dice Ibegon at Audience Chamber. Poison stinger cumulatively adds 1 to attrition against opponent in battles at same site.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.FLORN_LAMPROID);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.and(Filters.site, Filters.or(Filters.forest, Filters.jungle))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whileDiceIbegonAtAudienceChamber = new AtCondition(self, Filters.Dice_Ibegon, Filters.Audience_Chamber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, whileDiceIbegonAtAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, whileDiceIbegonAtAudienceChamber, 2));
        modifiers.add(new AttritionModifier(self, new InBattleAtCondition(self, Filters.site), 1, game.getOpponent(self.getOwner()), true));
        return modifiers;
    }
}
