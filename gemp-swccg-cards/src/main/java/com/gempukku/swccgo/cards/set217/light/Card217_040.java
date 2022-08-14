package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * SubType: Alien
 * Title: Mara Jade
 */
public class Card217_040 extends AbstractAlien {
    public Card217_040() {
        super(Side.LIGHT, 1, 5, 4, 5, 7, "Mara Jade", Uniqueness.UNIQUE);
        setLore("Female smuggler.");
        setGameText("[Pilot] 2. While Luke or Talon Karrde on table, power +1 and she moves for free. Anakin's Lightsaber may deploy on your Mara. Immune to attrition < 4.");
        addKeywords(Keyword.FEMALE, Keyword.SMUGGLER);
        addPersona(Persona.MARA_JADE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
        setMatchingWeaponFilter(Filters.persona(Persona.ANAKINS_LIGHTSABER));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new OnTableCondition(self, Filters.or(Filters.Luke, Filters.title("Talon Karrde"))), 1));
        modifiers.add(new MovesForFreeModifier(self, new OnTableCondition(self, Filters.or(Filters.Luke, Filters.title("Talon Karrde")))));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.persona(Persona.ANAKINS_LIGHTSABER), Filters.and(Filters.your(self), Filters.Mara_Jade)));
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.your(self), Filters.Mara_Jade), Filters.persona(Persona.ANAKINS_LIGHTSABER)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
