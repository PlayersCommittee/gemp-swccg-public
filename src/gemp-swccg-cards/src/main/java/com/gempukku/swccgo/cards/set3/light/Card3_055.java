package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: System
 * Title: Hoth
 */
public class Card3_055 extends AbstractSystem {
    public Card3_055() {
        super(Side.LIGHT, Title.Hoth, 5, ExpansionSet.HOTH, Rarity.U2);
        setLocationDarkSideGameText("If you control, for each of your starships here, your total power is +1 in battles at Hoth sites.");
        setLocationLightSideGameText("If you control, for each of your starships here, your total power is +1 in battles at Hoth sites.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.HOTH, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Hoth_site,
                Filters.locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(playerOnDarkSideOfLocation)),
                new ControlsCondition(playerOnDarkSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship)),
                playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Hoth_site,
                Filters.locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(playerOnLightSideOfLocation)),
                new ControlsCondition(playerOnLightSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship)),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}