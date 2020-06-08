package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Elsek
 */
public class Card8_112 extends AbstractImperial {
    public Card8_112() {
        super(Side.DARK, 3, 1, 1, 1, 3, "Sergeant Elsek", Uniqueness.UNIQUE);
        setLore("Stormtrooper biker scout. Kuat native. Avarik's partner since graduation from Corulag academy. Often forced to cover for partner's rash decisions.");
        setGameText("Adds 3 to power of any speeder bike he pilots. When in battle with Avarik, adds 2 to your total battle destiny. When forfeited at same site as your other biker scout, also satisfies all remaining attrition and battle damage against you.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atSameSiteAsYourOtherBikerScout = new AtSameSiteAsCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.biker_scout));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.speeder_bike));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.Avarik), 2, self.getOwner()));
        modifiers.add(new SatisfiesAllBattleDamageWhenForfeitedModifier(self, atSameSiteAsYourOtherBikerScout));
        modifiers.add(new SatisfiesAllAttritionWhenForfeitedModifier(self, atSameSiteAsYourOtherBikerScout));
        return modifiers;
    }
}
