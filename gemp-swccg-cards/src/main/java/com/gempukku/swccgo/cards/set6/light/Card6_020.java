package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ithorian
 */
public class Card6_020 extends AbstractAlien {
    public Card6_020() {
        super(Side.LIGHT, 2, 2, 1, 2, 2, "Ithorian", Uniqueness.RESTRICTED_3);
        setLore("Good natured, optimistic herders from Ithor. Plant trees and reforest areas. Sided with Rebellion at great risk to their home planet.");
        setGameText("Power and forfeit +2 while Momaw Nadon at Audience Chamber. While at any jungle, swamp, forest or exterior Endor site, adds one [Dark Side Force] icon and one [Light Side Force] icon. Prevents [Selective Creature] creatures from attacking where present.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.ITHORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whileMomawNadonAtAudienceChamber = new AtCondition(self, Filters.Momaw_Nadon, Filters.Audience_Chamber);
        Condition atJungleSwampForestOrExteriorEndorSite = new AtCondition(self, Filters.or(Filters.jungle, Filters.swamp,
                Filters.forest, Filters.exterior_Endor_site));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, whileMomawNadonAtAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, whileMomawNadonAtAudienceChamber, 2));
        modifiers.add(new IconModifier(self, Filters.sameSite(self), atJungleSwampForestOrExteriorEndorSite, Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, Filters.sameSite(self), atJungleSwampForestOrExteriorEndorSite, Icon.LIGHT_FORCE, 1));
        modifiers.add(new MayNotAttackModifier(self, Filters.and(Icon.SELECTIVE_CREATURE, Filters.creature, Filters.at(Filters.wherePresent(self)))));
        return modifiers;
    }
}
