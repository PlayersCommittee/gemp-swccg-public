package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Starfighter
 * Title: Queen's Royal Starship
 */
public class Card12_091 extends AbstractStarfighter {
    public Card12_091() {
        super(Side.LIGHT, 2, 4, 3, 5, null, 7, 7, "Queen's Royal Starship", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Chromium-plated, sleek transport ship used by the royalty of the Naboo. Spaceframe was designed around a J-type configuration.");
        setGameText("May add 2 pilots and 5 passengers. Weapons may not deploy on this starship. While Ric piloting, draws one battle destiny if unable to otherwise, and immune to Lateral Damage and attrition < 4.");
        addPersona(Persona.QUEENS_ROYAL_STARSHIP);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addModelType(ModelType.J_TYPE_327_NUBIAN);
        setPilotCapacity(2);
        setPassengerCapacity(5);
        setMatchingPilotFilter(Filters.Ric);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition ricPiloting = new HasPilotingCondition(self, Filters.Ric);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.weapon_or_character_with_permanent_weapon, self));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, ricPiloting, 1));
        modifiers.add(new ImmuneToTitleModifier(self, ricPiloting, Title.Lateral_Damage));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, ricPiloting, 4));
        return modifiers;
    }
}
