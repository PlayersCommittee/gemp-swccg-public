package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Transport
 * Title: Wittin's Sandcrawler
 */
public class Card7_318 extends AbstractTransportVehicle {
    public Card7_318() {
        super(Side.DARK, 2, 3, 3, 4, null, 2, 5, "Wittin's Sandcrawler", Uniqueness.UNIQUE);
        setLore("Patrols the Dune Sea searching for lost droids and other items worth scavenging. Armor reinforced to protect against krayt dragon attacks. Enclosed.");
        setGameText("Deploys only on Tatooine. May add 1 driver and 7 passengers. While your Jawa is aboard, this vehicle and your Jawas at same site are immune to attrition < 3. Power +2 if Wittin at same site.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SANDCRAWLER, Keyword.ENCLOSED);
        setDriverCapacity(1);
        setPassengerCapacity(7);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(self, Filters.and(Filters.your(self), Filters.Jawa,
                Filters.atSameSite(self))), new HasAboardCondition(self, Filters.and(Filters.your(self), Filters.Jawa)), 3));
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Wittin), 2));
        return modifiers;
    }
}
