package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Navy Trooper Shield Technician
 */
public class Card8_109 extends AbstractImperial {
    public Card8_109() {
        super(Side.DARK, 3, 2, 1, 1, 2, "Navy Trooper Shield Technician", Uniqueness.RESTRICTED_3);
        setLore("Many Imperial Navy troopers receive technical as well as combat training, which they use to maintain the Empire's military facilities.");
        setGameText("Forfeit +2 while with Hewex. When at Bunker (or aboard a Star Destroyer at Endor), cumulatively adds 1 to deploy cost of each opponent's starship and non-Ewok vehicle deploying to Endor (or to your mobile site orbiting Endor)");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new WithCondition(self, Filters.Hewex), 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.starship, Filters.non_Ewok_vehicle)),
                new OrCondition(new AtCondition(self, Filters.Bunker), new AboardCondition(self, Filters.and(Filters.Star_Destroyer, Filters.at(Filters.Endor_system)))),
                1, Filters.or(Filters.Endor_location, Filters.and(Filters.mobile_site, Filters.relatedLocationTo(self, Filters.isOrbiting(Title.Endor)))), true));
        return modifiers;
    }
}
