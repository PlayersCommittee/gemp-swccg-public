package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Character
 * Subtype: Rebel
 * Title: Zeb Orrelios
 */
public class Card208_013 extends AbstractRebel {
    public Card208_013() {
        super(Side.LIGHT, 2, 4, 5, 2, 5, Title.Zeb, Uniqueness.UNIQUE);
        setLore("Lasat.");
        setGameText("[Pilot] 1. During battle with an opponent's combat vehicle (or with two Rebels), attrition against opponent is +2. Rebels here are immune to Trample.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        setSpecies(Species.LASAT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new AttritionModifier(self, new OrCondition(new InBattleWithCondition(self, Filters.and(Filters.opponents(self), Filters.combat_vehicle)),
                new InBattleWithCondition(self, 2, Filters.Rebel)), 2, opponent));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.Rebel, Filters.here(self)), Title.Trample));
        return modifiers;
    }
}
