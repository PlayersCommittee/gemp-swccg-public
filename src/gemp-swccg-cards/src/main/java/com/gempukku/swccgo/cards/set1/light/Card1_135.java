package com.gempukku.swccgo.cards.set1.light;

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
 * Set: Premiere
 * Type: Location
 * Subtype: System
 * Title: Yavin 4
 */
public class Card1_135 extends AbstractSystem {
    public Card1_135() {
        super(Side.LIGHT, Title.Yavin_4, 4, ExpansionSet.PREMIERE, Rarity.C2);
        setLocationDarkSideGameText("If you control, for each of your starships here, your total power is +1 in battles at Yavin 4 sites.");
        setLocationLightSideGameText("If you control, for each of your starships here, your total power is +1 in battles at Yavin 4 sites.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Yavin_4_site, Filters.battleLocation),
                new ControlsCondition(playerOnDarkSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship)),
                playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.Yavin_4_site, Filters.battleLocation),
                new ControlsCondition(playerOnLightSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship)),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}