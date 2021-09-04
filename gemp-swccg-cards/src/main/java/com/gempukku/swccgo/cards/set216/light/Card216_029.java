package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayUnlessImmuneToSpecificTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Grogu
 */
public class Card216_029 extends AbstractAlien {
    public Card216_029() {
        super(Side.LIGHT, 2, 4, 2, 4, 6, "Grogu", Uniqueness.UNIQUE);
        setLore("");
        setGameText("While defending a battle, Interrupts may not be played unless they are [Immune to Sense]. Opponent may not target Grogu with weapons unless each of your Mandalorians and non-[Episode I] Jedi present are 'hit.' Immune to attrition < 3.");
        addPersona(Persona.GROGU);
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotPlayUnlessImmuneToSpecificTitleModifier(self, Filters.Interrupt, new DefendingBattleCondition(self), Title.Sense));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.persona(Persona.GROGU), new PresentAtCondition(Filters.and(Filters.your(self), Filters.or(Filters.Mandalorian, Filters.and(Filters.not(Icon.EPISODE_I), Filters.Jedi))), Filters.here(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
