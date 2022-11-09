package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Lando's Wrist Comlink
 */
public class Card5_014 extends AbstractCharacterDevice {
    public Card5_014() {
        super(Side.LIGHT, 4, "Lando's Wrist Comlink", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("Easily concealed device used by Lando to remain in contact with his subordinates. Directly connected to Cloud City's central computer.");
        setGameText("Deploy on a character. When at a Cloud City site, Lobot and your Cloud City troopers may deploy or move here as a 'react.'");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atCloudCitySite = new AtCondition(self, Filters.Cloud_City_site);
        Filter lobotAndCloudCityTroopers = Filters.and(Filters.your(self), Filters.or(Filters.Lobot, Filters.Cloud_City_trooper));
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy Lobot or a Cloud City trooper as a 'react'",
                atCloudCitySite, playerId, lobotAndCloudCityTroopers, here));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move Lobot or a Cloud City trooper as a 'react'",
                atCloudCitySite, playerId, lobotAndCloudCityTroopers, here));
        return modifiers;
    }
}