package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: System
 * Title: Endor
 */
public class Card8_157 extends AbstractSystem {
    public Card8_157() {
        super(Side.DARK, Title.Endor, 8);
        setLocationDarkSideGameText("If you control, for each of your starships here, your total power is +1 in battles at Endor sites.");
        setLocationLightSideGameText("If you have no Ewoks on Endor, Force drain -1 here. To move or deploy your starship to here requires +1 Force.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Endor_site, Filters.battleLocation),
                new ControlsCondition(playerOnDarkSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship)),
                playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarships = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new CantSpotCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.Ewok, Filters.on(Title.Endor))), -1, playerOnLightSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, yourStarships, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, yourStarships, 1, self));
        return modifiers;
    }
}