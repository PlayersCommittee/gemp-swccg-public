package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Lobot
 */
public class Card304_061 extends AbstractAlien {
    public Card304_061() {
        super(Side.LIGHT, 1, 2, 0, 2, 5, "Lobot", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Former criminal. Fitted with a cybernetic device. Now repays his debt to society by ensuring the smooth running of Cloud City. Becomes disoriented when not with a computer.");
        setGameText("When present at a Scomp link, power +2 and opponent's total power is -2 at same site. ");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentAtScompLink = new PresentAtScompLink(self);
        Filter yourCloudCityTroopers = Filters.and(Filters.your(self), Filters.Locita);
        Condition atCloudCitySite = new AtCondition(self, Filters.Cloud_City_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, presentAtScompLink, 2));
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self), presentAtScompLink, -2, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
