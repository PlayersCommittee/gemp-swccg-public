package com.gempukku.swccgo.cards.set5.light;

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
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Lobot
 */
public class Card5_006 extends AbstractAlien {
    public Card5_006() {
        super(Side.LIGHT, 1, 2, 0, 2, 5, "Lobot", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Former criminal. Fitted with a cybernetic device. Now repays his debt to society by ensuring the smooth running of Cloud City. Becomes disoriented when not with a computer.");
        setGameText("Deploys only as a 'react' to a Cloud City site or to where your Lando is present. When present at a Scomp link, power +2 and opponent's total power is -2 at same site. Your Cloud City Troopers deploy free and are power +1 at same Cloud City site.");
        addPersona(Persona.LOBOT);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (asReact)
            return Filters.locationAndCardsAtLocation(Filters.or(Filters.Cloud_City_site, Filters.wherePresent(self, Filters.Lando)));
        else
            return Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.or(Filters.Cloud_City_site, Filters.wherePresent(self, Filters.Lando))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentAtScompLink = new PresentAtScompLink(self);
        Filter yourCloudCityTroopers = Filters.and(Filters.your(self), Filters.Cloud_City_trooper);
        Condition atCloudCitySite = new AtCondition(self, Filters.Cloud_City_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, presentAtScompLink, 2));
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self), presentAtScompLink, -2, game.getOpponent(self.getOwner())));
        modifiers.add(new DeploysFreeToLocationModifier(self, yourCloudCityTroopers, atCloudCitySite, Filters.here(self)));
        modifiers.add(new PowerModifier(self, Filters.and(yourCloudCityTroopers, Filters.here(self)), atCloudCitySite, 1));
        return modifiers;
    }
}
