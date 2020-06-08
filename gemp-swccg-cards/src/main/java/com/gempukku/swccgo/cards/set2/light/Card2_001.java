package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAwayAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Arcona
 */
public class Card2_001 extends AbstractAlien {
    public Card2_001() {
        super(Side.LIGHT, 3, 2, 1, 1, 2, "Arcona", Uniqueness.RESTRICTED_3);
        setLore("Unut Poll is a typical male Arcona. Unlike many other Arcona, he has avoided salt. Scout known to cooperate with Alliance operatives.");
        setGameText("Power +1 under 'nighttime conditions.' May move away from a battle at same site as a 'react.'");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.ARCONA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new UnderNighttimeConditionConditions(self), 1));
        modifiers.add(new MayMoveAwayAsReactModifier(self, new InBattleAtCondition(self, Filters.site)));
        return modifiers;
    }
}
