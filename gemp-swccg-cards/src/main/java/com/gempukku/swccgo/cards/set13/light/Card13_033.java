package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.InLightsaberCombatCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Rebel
 * Title: Obi-Wan Kenobi, Jedi Knight
 */
public class Card13_033 extends AbstractRepublic {
    public Card13_033() {
        super(Side.LIGHT, 1, 7, 6, 6, 8, "Obi-Wan Kenobi, Jedi Knight", Uniqueness.UNIQUE);
        setLore("Padawan learner promoted to Jedi Knight after his encounter with Darth Maul. Has sworn to train Anakin Skywalker, even if the Jedi Council forbids it.");
        setGameText("Deploys -2 to Naboo. Power +2 if Maul present. If Qui-Gon is participating in lightsaber combat and Obi-Wan is present, Qui-Gon may use Obi-Wan's combat cards. Immune to Sniper and attrition < 5.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Naboo));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.Maul), 2));
        modifiers.add(new MayUseOtherCharactersCombatCardsModifier(self, Filters.QuiGon,
                new AndCondition(new InLightsaberCombatCondition(self, Filters.QuiGon),
                        new PresentAtCondition(self, Filters.sameLocationAs(self, Filters.QuiGon))), self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sniper));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
